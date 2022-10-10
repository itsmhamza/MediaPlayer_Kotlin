package com.example.mediaplayer.fragments.Video

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.Data.getAllVideo
import com.example.mediaplayer.PlayerActivity
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentVideosBinding
import com.example.mediaplayer.databinding.RenameViewBinding
import com.example.mediaplayer.interfaces.onItemRename
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class videos : Fragment(), onItemRename {

    private lateinit var binding: FragmentVideosBinding
        @SuppressLint("StaticFieldLeak")
        lateinit var adopter: VideoRAdopter
        @SuppressLint("StaticFieldLeak")
        lateinit var adopter2: VideoRAdopterg
             var myposition: Int = 0
    private lateinit var dialogRF:androidx.appcompat.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_videos, container, false)
        binding = FragmentVideosBinding.bind(view)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.videoRecyclerview.setHasFixedSize(true)
        binding.videoRecyclerview.setItemViewCacheSize(10)
        adopter2 = VideoRAdopterg(requireContext(), Video.videoList,false,this)
        adopter  = VideoRAdopter(requireContext(), Video.videoList,false,this)
        binding.videoRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.videoRecyclerview.adapter = adopter
        val grid = activity?.getSharedPreferences("grid",Context.MODE_PRIVATE)?.getInt("edit",0)
        if (grid==1) {
            binding.videoRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.videoRecyclerview.adapter = adopter2
        }
        //pref
        //refresh
        binding.root.setOnRefreshListener {
            Video.videoList = getAllVideo(requireContext())
            adopter.updateList(Video.videoList)
            adopter2.updateList(Video.videoList)
            binding.root.isRefreshing = false
        }
        binding.nowplay.setOnClickListener {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra("class", "NowPlaying")
            startActivity(intent)
        }
        return binding.root
    }
    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    override fun onResume() {
        super.onResume()
        if (PlayerActivity.position != -1)
            binding.nowplay.visibility = View.VISIBLE
        if (Video.adopterchanged) binding.videoRecyclerview.adapter?.notifyDataSetChanged()
        Video.adopterchanged = false
        adopter.notifyDataSetChanged()
        adopter2.notifyDataSetChanged()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val grid = activity?.getSharedPreferences("grid",Context.MODE_PRIVATE)?.getInt("edit",0)
        if (grid==1)
        {
            binding.toolbar.menu.findItem(R.id.grid).setIcon(resources.getDrawable(R.drawable.ic_grid_view))
            binding.toolbar.menu.findItem(R.id.list).setIcon(resources.getDrawable(R.drawable.ic_listview_b_w))
        }else{
            binding.toolbar.menu.findItem(R.id.list).setIcon(resources.getDrawable(R.drawable.ic_listview_b))
            binding.toolbar.menu.findItem(R.id.grid).setIcon(resources.getDrawable(R.drawable.ic_grid_view_w))
        }
           val searchView = menu.findItem(R.id.search_view)?.actionView as SearchView
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
                       adopter.updateList(searchList = Video.searchList)
                       adopter2.updateList(searchList = Video.searchList)
                   }
                   return true
               }
           })
        super.onCreateOptionsMenu(menu, inflater)
    }
    @SuppressLint("ResourceType", "UseCompatLoadingForDrawables")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.grid ->
            {
             activity?.getSharedPreferences("grid",Context.MODE_PRIVATE)?.edit()
                 ?.putInt("edit",1)?.apply()
                binding.videoRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)
                binding.videoRecyclerview.adapter = adopter2
                item.setIcon(resources.getDrawable(R.drawable.ic_grid_view))
                binding.toolbar.menu.findItem(R.id.list).setIcon(resources.getDrawable(R.drawable.ic_listview_b_w))
            }
            R.id.list ->
                {
                activity?.getSharedPreferences("grid",Context.MODE_PRIVATE)?.edit()
                    ?.putInt("edit",0)?.apply()
                binding.videoRecyclerview.layoutManager = LinearLayoutManager(requireContext())
                binding.videoRecyclerview.adapter = adopter
                item.setIcon(resources.getDrawable(R.drawable.ic_listview_b))
                binding.toolbar.menu.findItem(R.id.grid).setIcon(resources.getDrawable(R.drawable.ic_grid_view_w))
                }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun position(position: Int) {
        myposition = position
        requestWrite(position)

    }

    override fun delposition(position: Int) {
        myposition =position
        try {
            requestDelete(position)
        }catch (e:Exception){}
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
                     if (newName != null && currentFile.exists() && newName.toString()
                                .isNotEmpty()
                        ){
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
                                    it.put(
                                        MediaStore.Files.FileColumns.DISPLAY_NAME,
                                        newName.toString()
                                    )
                                    it.put(MediaStore.Files.FileColumns.IS_PENDING, 0)
                                    requireContext().contentResolver.update(fromUri, it, null, null)
                            }
                            updateRenameUI(
                                position,
                                newName = newName.toString(),
                                newFile = newFile
                            )
                        }
                    self.dismiss()

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
                    if(currentFile.extension == "flv" || currentFile.extension == "3gp"){
                        Toast.makeText(context, "Could't renamed", Toast.LENGTH_SHORT).show()
                    }
                    else
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
                adopter.notifyItemChanged(position)
                adopter2.notifyItemChanged(position)
            }
          /* isFolder -> {
                folderActivity.currentfolderVideos[position].title = newName
                folderActivity.currentfolderVideos[position].path = newFile.path
                folderActivity.currentfolderVideos[position].artUri = Uri.fromFile(newFile)
                adopter.notifyItemChanged(position)
                Video.datachanged = true
            }*/
            else -> {
                Video.videoList[position].title = newName
                Video.videoList[position].path = newFile.path
                Video.videoList[position].artUri = Uri.fromFile(newFile)
                adopter.notifyItemChanged(position)
                adopter2.notifyItemChanged(position)

            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
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
                adopter.notifyDataSetChanged()
                adopter2.notifyDataSetChanged()
            }
           /* isFolder -> {
                Video.datachanged = true
                folderActivity.currentfolderVideos.removeAt(position)
                adopter.notifyDataSetChanged()
            }*/
            else -> {
                Video.videoList.removeAt(position)
                adopter.notifyDataSetChanged()
                adopter2.notifyDataSetChanged()
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