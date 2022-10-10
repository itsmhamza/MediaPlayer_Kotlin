package com.example.mediaplayer.Home

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.Data.getAllVideo
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentHomeBinding
import com.example.mediaplayer.databinding.RenameViewBinding
import com.example.mediaplayer.fragments.Video.FolderRAdopter
import com.example.mediaplayer.fragments.Video.Video
import com.example.mediaplayer.fragments.Video.VideoRAdopterg
import com.example.mediaplayer.interfaces.onItemRename
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class Home : Fragment(),onItemRename {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var folderRAdopter: FolderRAdopter
    private lateinit var videoRAdopter: VideoRAdopterg
    private var myposition:Int=0
    private lateinit var dialogRF:androidx.appcompat.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Video.folderList = ArrayList()
        Video.videoList = getAllVideo(requireContext())
        if (Video.folderList.isNotEmpty()){
            folderRAdopter = FolderRAdopter(requireContext(), Video.folderList)
            binding.recyclerView.adapter=folderRAdopter
        }
        if (Video.videoList.isNotEmpty()){
            videoRAdopter = VideoRAdopterg(requireContext(), Video.videoList,false,this)
            binding.recyclerViewv.adapter=videoRAdopter
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        binding = FragmentHomeBinding.bind(view)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerViewv.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        return view
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.viewall_video,menu)
        val searchView = menu.findItem(R.id.search_home)?.actionView as SearchView
        searchView.setBackgroundResource(R.drawable.custom_search_view)
        searchView.setQueryHint("Search Videos")
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    Video.searchList = ArrayList()
                    for (video in Video.videoList) {
                        if (video.title.lowercase().contains(newText.lowercase()))
                            Video.searchList.add(video)
                    }
                    Video.search = true
                    try {
                        videoRAdopter.updateList(searchList = Video.searchList)
                    }catch (e:Exception){}
                }
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.view_all -> replaceFragment(Video())
        }
        return super.onOptionsItemSelected(item)
    }
    private fun replaceFragment(fragment: Fragment) {
        val transcation = parentFragmentManager.beginTransaction()
        transcation.replace(R.id.fragmentContainerView,fragment)
        transcation.commit()
    }

    override fun position(position: Int) {
        myposition = position
        requestWrite(position)
    }

    override fun delposition(position: Int) {
        myposition = position
        requestDelete(position)
    }

    fun requestWrite(newposition: Int) {
        val uriList: List<Uri> = listOf(
            Uri.withAppendedPath(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                Video.videoList[newposition].id))

        //requesting file write permission for specific files
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val pi = MediaStore.createWriteRequest(requireContext().contentResolver, uriList)
            startIntentSenderForResult(pi.intentSender, 124,
                null, 0, 0, 0, null)
        }
        else renameFunction(newposition)
    }
    private fun renameFunction(position: Int) {
        val customDialogRF = LayoutInflater.from(requireContext()).inflate(R.layout.rename_view,
            (requireContext() as Activity).findViewById(R.id.drawerlayoutaa),false)
        val bindingRF = RenameViewBinding.bind(customDialogRF)
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.R){
            dialogRF = MaterialAlertDialogBuilder(requireContext()).setView(customDialogRF)
                .setCancelable(false)
                .setPositiveButton("Rename") { self, _ ->
                    val currentFile = File(Video.videoList[position].path)

                    val newName = bindingRF.renameField.text
                    if(currentFile.extension == "flv"){
                        Toast.makeText(context, "Could't renamed", Toast.LENGTH_SHORT).show()
                    }else
                    if (newName != null && currentFile.exists() && newName.toString().isNotEmpty()) {
                        val newFile = File(
                            currentFile.parentFile,
                            newName.toString() + "." + currentFile.extension
                        )
                        val fromUri = Uri.withAppendedPath(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            Video.videoList[position].id
                        )
                        ContentValues().also {
                            it.put(MediaStore.Files.FileColumns.IS_PENDING, 1)
                            requireContext().contentResolver.update(fromUri, it, null, null)
                            it.clear()
                            //updating file details
                            it.put(MediaStore.Files.FileColumns.DISPLAY_NAME, newName.toString())
                            it.put(MediaStore.Files.FileColumns.IS_PENDING, 0)
                            requireContext().contentResolver.update(fromUri, it, null, null)
                        }
                        updateRenameUI(position, newName = newName.toString(), newFile = newFile)
                    }
                    self.dismiss()
//                updateList(Video.videoList)
//                notifyDataSetChanged()
                }
                .setNegativeButton("Cancel") { self, _ ->
                    self.dismiss()
                }
                .create()
        }
        else
        {
            dialogRF = MaterialAlertDialogBuilder(requireContext()).setView(customDialogRF)
                .setCancelable(false)
                .setPositiveButton("Rename"){ self, _ ->
                    val currentFile = File(Video.videoList[position].path)
                    val newName = bindingRF.renameField.text
                    if(currentFile.extension == "flv"){
                        Toast.makeText(context, "Could't renamed", Toast.LENGTH_SHORT).show()
                    }else
                    if(newName != null && currentFile.exists() && newName.toString().isNotEmpty()){
                        val newFile = File(currentFile.parentFile, newName.toString()+"."+currentFile.extension)
                        if(currentFile.renameTo(newFile)){
                            MediaScannerConnection.scanFile(requireContext(), arrayOf(newFile.toString()), arrayOf("video/*"), null)
                            updateRenameUI(position = position, newName = newName.toString(), newFile = newFile)
                        }
                    }
                    self.dismiss()
                }
                .setNegativeButton("Cancel"){self, _ ->
                    self.dismiss()
                }
                .create()
        }
        bindingRF.renameField.setText(Video.videoList[position].title)
        dialogRF.show()
        dialogRF.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(
            MaterialColors.getColor(requireContext(),R.attr.useMaterialThemeColors,
                Color.TRANSPARENT))
        dialogRF.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(
            MaterialColors.getColor(requireContext(),R.attr.useMaterialThemeColors,
                Color.TRANSPARENT))
    }
    private fun updateRenameUI(position: Int, newName: String, newFile: File){
        when{
            Video.search -> {
                Video.searchList[position].title = newName
                Video.searchList[position].path = newFile.path
                Video.searchList[position].artUri = Uri.fromFile(newFile)
                videoRAdopter.notifyItemChanged(position)

            }
//         //   isFolder -> {
//                folderActivity.currentfolderVideos[position].title = newName
//                folderActivity.currentfolderVideos[position].path = newFile.path
//                folderActivity.currentfolderVideos[position].artUri = Uri.fromFile(newFile)
//                notifyItemChanged(position)
//                Video.datachanged = true
//            }
            else -> {
                Video.videoList[position].title = newName
                Video.videoList[position].path = newFile.path
                Video.videoList[position].artUri = Uri.fromFile(newFile)
                videoRAdopter.notifyItemChanged(position)
            }
        }
    }
    private fun requestDelete(position: Int) {
        //list of videos to delete
        val uriList: List<Uri> = listOf(
            Uri.withAppendedPath(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                Video.videoList[position].id
            )
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //requesting for delete permission
            val pi = MediaStore.createDeleteRequest(requireContext().contentResolver, uriList)
            startIntentSenderForResult(
                pi.intentSender, 123,
                null, 0, 0, 0, null
            )
        } else {
            //for devices less than android 11
            val file = File(Video.videoList[position].path)
            val builder = MaterialAlertDialogBuilder(requireContext())
            builder.setTitle("Delete Video?")
                .setMessage(Video.videoList[position].title)
                .setPositiveButton("Yes") { self, _ ->
                    if (file.exists() && file.delete()) {
                        MediaScannerConnection.scanFile(requireContext(), arrayOf(file.path), null, null)
                        updateDeleteUI(position = position)
                    }
                    self.dismiss()
                }
                .setNegativeButton("No") { self, _ -> self.dismiss() }
            val delDialog = builder.create()
            delDialog.show()
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun updateDeleteUI(position: Int){
        when{
            Video.search -> {
                Video.datachanged = true
                Video.videoList.removeAt(position)
                videoRAdopter.notifyDataSetChanged()
            }
            else -> {
                Video.videoList.removeAt(position)
                videoRAdopter.notifyDataSetChanged()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onResult(requestCode,resultCode)

    }
    fun onResult(requestCode: Int, resultCode: Int){
        when(requestCode){
            124 -> if(resultCode == Activity.RESULT_OK) renameFunction(myposition)
            123 -> {
                if(resultCode == Activity.RESULT_OK) updateDeleteUI(myposition)
            }
        }
    }
}