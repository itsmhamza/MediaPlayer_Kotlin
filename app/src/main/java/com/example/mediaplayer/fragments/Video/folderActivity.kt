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
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.Data.video
import com.example.mediaplayer.MainActivity
import com.example.mediaplayer.R
import com.example.mediaplayer.Themes.Themes
import com.example.mediaplayer.databinding.ActivityFolderBinding
import com.example.mediaplayer.databinding.RenameViewBinding
import com.example.mediaplayer.interfaces.onItemRename
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class folderActivity() : AppCompatActivity() ,onItemRename{
    lateinit var binding:ActivityFolderBinding
    lateinit var adopter:VideoRAdopter
    private var myposition:Int=0
    private var isFolder:Boolean=true
    private lateinit var dialogRF:androidx.appcompat.app.AlertDialog
    companion object{
        lateinit var currentfolderVideos:ArrayList<video>
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val pref = this.getSharedPreferences("smartbar", Context.MODE_PRIVATE)
        MainActivity.currenttheme = pref?.getString("theme", "0").toString()
        setthemeActivity()
        val position = intent.getIntExtra("position",0)
        currentfolderVideos = getAllVideo(Video.folderList[position].id)
        binding.toolbar.title = Video.folderList[position].folderName
        binding.foldervRecyclerview.setHasFixedSize(true)
        binding.foldervRecyclerview.layoutManager = LinearLayoutManager(this@folderActivity)
        adopter = VideoRAdopter(this@folderActivity, currentfolderVideos,isFolder = true, this)
        binding.foldervRecyclerview.adapter =  adopter
    }
    fun setthemeActivity() {
        val pref = this.getSharedPreferences("smartbar", Context.MODE_PRIVATE)
        Themes.currenttheme = pref?.getString("theme","0").toString()
        if (Themes.currenttheme.toInt() ==0){
            binding.root.setBackgroundResource(R.color.bg_color)
        }
        else if (Themes.currenttheme.toInt() ==1){
            binding.root.setBackgroundResource(R.drawable.bg1)
        }
        else if (Themes.currenttheme.toInt() ==2){
            binding.root.setBackgroundResource(R.drawable.bg2)
        }
        else if (Themes.currenttheme.toInt() ==3){
            binding.root.setBackgroundResource(R.drawable.bg3)
        }
        else if (Themes.currenttheme.toInt() ==4){
            binding.root.setBackgroundResource(R.drawable.bg4)
        }
        else if (Themes.currenttheme.toInt() ==5){
            binding.root.setBackgroundResource(R.drawable.bg5)
        }
        else if (Themes.currenttheme.toInt() ==6){
            binding.root.setBackgroundResource(R.drawable.bg6)
        }
        else if (Themes.currenttheme.toInt() ==7){
            binding.root.setBackgroundResource(R.drawable.bg7)
        }
    }
    @SuppressLint("Range")
     fun getAllVideo(folderId:String):ArrayList<video>{
        val tempList = ArrayList<video>()
        val selection = MediaStore.Video.Media.BUCKET_ID + " like? "
        val projection = arrayOf(
            MediaStore.Video.Media.TITLE, MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media._ID, MediaStore.Video.Media.BUCKET_DISPLAY_NAME
            , MediaStore.Video.Media.DATA, MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.BUCKET_ID)
        val cursor = this.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,projection,
            selection, arrayOf(folderId), MediaStore.Video.Media.DATE_ADDED + " DESC")
        if (cursor!=null)
            if (cursor.moveToNext())
                do {
                    val titleC =  cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))?:"Unknown"
                    val idC =  cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))?:"Unknown"
                    val folderC =  cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))?:"Internal Storage"
                    val sizeC =  cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))?:"0"
                    val folderIdC =  cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID))?:"Unknown"
                    val pathC =  cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))?:"Unknown"
                    val durationC =  cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))?.toLong()?:0L
                    try {
                        val file = File(pathC)
                        val artUriC = Uri.fromFile(file)
                        val video = video(title = titleC,id = idC,folderName = folderC,
                            duration = durationC,size = sizeC,path = pathC,artUri = artUriC,)
                        if (file.exists()) tempList.add(video)
                    }catch (e:Exception){}
                } while (cursor.moveToNext())
        cursor?.close()
        return tempList
    }

    override fun position(position: Int) {
        myposition=position
        requestWrite(position)
    }

    override fun delposition(position: Int) {
        myposition=position
        requestDelete(position)
    }

    fun requestWrite(newposition: Int){
        val uriList: List<Uri> = listOf(Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            Video.videoList[newposition].id))
        //requesting file write permission for specific files
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val pi = MediaStore.createWriteRequest(this.contentResolver, uriList)
            this.startIntentSenderForResult(pi.intentSender, 125,
                null, 0, 0, 0, null)
        }
        else renameFunction(newposition)
    }
    private fun renameFunction(position: Int) {
        val customDialogRF = LayoutInflater.from(this).inflate(R.layout.rename_view,
            (this as Activity).findViewById(R.id.folderrr),false)
        val bindingRF = RenameViewBinding.bind(customDialogRF)
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.R){
            dialogRF = MaterialAlertDialogBuilder(this).setView(customDialogRF)
                .setCancelable(false)
                .setPositiveButton("Rename") { self, _ ->
                    val currentFile = File(Video.videoList[position].path)
                    val newName = bindingRF.renameField.text
                    if(currentFile.extension == "flv"){
                        Toast.makeText(this, "Could't renamed", Toast.LENGTH_SHORT).show()
                    }else
                        if (currentFile.name == newName.toString()){
                            Toast.makeText(this, "Already name", Toast.LENGTH_SHORT).show()
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
                            this.contentResolver.update(fromUri, it, null, null)
                            it.clear()
                            //updating file details
                            it.put(MediaStore.Files.FileColumns.DISPLAY_NAME, newName.toString())
                            it.put(MediaStore.Files.FileColumns.IS_PENDING, 0)
                            this.contentResolver.update(fromUri, it, null, null)
                        }
                        updateRenameUI(position, newName = newName.toString(), newFile = newFile)
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
            dialogRF = MaterialAlertDialogBuilder(this).setView(customDialogRF)
                .setCancelable(false)
                .setPositiveButton("Rename"){ self, _ ->
                    val currentFile = File(Video.videoList[position].path)
                    val newName = bindingRF.renameField.text
                    if(currentFile.extension == "flv"){
                        Toast.makeText(this, "Could't renamed", Toast.LENGTH_SHORT).show()
                    }else
                    if(newName != null && currentFile.exists() && newName.toString().isNotEmpty()){
                        val newFile = File(currentFile.parentFile, newName.toString()+"."+currentFile.extension)
                        if(currentFile.renameTo(newFile)){
                            MediaScannerConnection.scanFile(this, arrayOf(newFile.toString()), arrayOf("video/*"), null)
                            isFolder=true
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
            MaterialColors.getColor(this,com.google.android.material.R.attr.useMaterialThemeColors,
            Color.TRANSPARENT))
        dialogRF.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(
            MaterialColors.getColor(this,com.google.android.material.R.attr.useMaterialThemeColors,
            Color.TRANSPARENT))
    }
    private fun updateRenameUI(position: Int, newName: String, newFile: File) {
        when {
            isFolder -> {
                currentfolderVideos[position].title = newName
                currentfolderVideos[position].path = newFile.path
                currentfolderVideos[position].artUri = Uri.fromFile(newFile)
                adopter.notifyItemChanged(position)
                Video.datachanged = true
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
            val pi = MediaStore.createDeleteRequest(this.contentResolver, uriList)
            this.startIntentSenderForResult(
                pi.intentSender, 126,
                null, 0, 0, 0, null
            )
        } else {
            //for devices less than android 11
            val file = File(Video.videoList[position].path)
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Delete Video?")
                .setMessage(Video.videoList[position].title)
                .setPositiveButton("Yes") { self, _ ->
                    if (file.exists() && file.delete()) {
                        MediaScannerConnection.scanFile(this, arrayOf(file.path), null, null)
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
            isFolder -> {
                Video.datachanged = true
                currentfolderVideos.removeAt(position)
                adopter.notifyDataSetChanged()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onResult(requestCode,resultCode)

    }
    fun onResult(requestCode: Int, resultCode: Int){
        when(requestCode){
            125 -> {
                if (resultCode == Activity.RESULT_OK) renameFunction(myposition)
            }
            126 -> {
                if(resultCode == Activity.RESULT_OK) updateDeleteUI(myposition)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}
