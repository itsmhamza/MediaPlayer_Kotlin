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
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.SpannableStringBuilder
import android.text.format.DateUtils
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mediaplayer.Data.video
import com.example.mediaplayer.PlayerActivity
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.DetailsViewBinding
import com.example.mediaplayer.databinding.RenameViewBinding
import com.example.mediaplayer.databinding.VideoMoreFeaturesBinding
import com.example.mediaplayer.databinding.VideoViewGridBinding
import com.example.mediaplayer.interfaces.onItemRename
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class VideoRAdopterg(private val content:Context,
                     private var videolist: ArrayList<video>, private val isFolder:Boolean=false,onItemRename: onItemRename
)
    :RecyclerView.Adapter<VideoRAdopterg.MyHolder>() {

    private var listener:onItemRename? = null
    private var newposition = 0
    private lateinit var dialogRF:androidx.appcompat.app.AlertDialog
    companion object {
        var media:Boolean=false
    }
    init {
        listener=onItemRename
    }
    class MyHolder(binding:VideoViewGridBinding):RecyclerView.ViewHolder(binding.root) {

        val title = binding.videoName
        val folder = binding.folderName
        val image = binding.image
        val size = binding.size
        val more = binding.more
        val root=binding.root

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(VideoViewGridBinding.inflate(LayoutInflater.from(content),parent,false))

    }
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: MyHolder, @SuppressLint("RecyclerView") position: Int) {
        val status =   content?.getSharedPreferences("media",Context.MODE_PRIVATE)?.getBoolean("status",true)
        if(status!!) {
            holder.title.isSelected=true
            holder.title.text = videolist[position].title
        }
        holder.folder.text = videolist[position].folderName
        holder.folder.isSelected= true
        holder.size.text = (Formatter.formatShortFileSize(content,videolist[position].size.toLong()))
        Glide.with(content)
            .asBitmap()
            .load(videolist[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_videob))
            .into(holder.image)
        holder.image.setOnClickListener {
           when{
               videolist[position].id == PlayerActivity.nowPlayingid ->{
                   sendintent(pos = position,ref = "NowPlaying")
               }
               Video.search ->{
                   PlayerActivity.pipstatus = 2
                   sendintent(pos = position,ref = "SearchedVideo")}
               isFolder ->{
                   PlayerActivity.pipstatus = 1
                   sendintent(pos = position,ref = "folderActivity")
               }else ->{
               PlayerActivity.pipstatus = 3
                   sendintent(pos = position,ref = "AllVideos")
               }
           }
        }
        holder.more.setOnClickListener {
            newposition = position
            val customDialog = LayoutInflater.from(this.content).inflate(R.layout.video_more_features,holder.root,false)
            val bindingMF = VideoMoreFeaturesBinding.bind(customDialog)
            val dialog = MaterialAlertDialogBuilder(this.content).setView(customDialog)
                .create()
            dialog.show()
            bindingMF.rename.setOnClickListener {
             //   requestPermissionR()
                dialog.dismiss()
                listener?.position(newposition)

            }
            bindingMF.details.setOnClickListener {
                dialog.dismiss()
                val customDialogIF = LayoutInflater.from(this.content).inflate(R.layout.details_view,holder.root,false)
                val bindingIF = DetailsViewBinding.bind(customDialogIF)
                val dialogIF = MaterialAlertDialogBuilder(this.content).setView(customDialogIF)
                    .setCancelable(false)
                    .setPositiveButton("Ok"){
                            self,_->
                        self.dismiss()
                    }
                    .create()
                dialogIF.show()
                val infoText = SpannableStringBuilder().bold { append("DETALS\n\nName: ") }.append(videolist[position].title)
                    .bold { append("\n\nDuration: ") }.append(DateUtils.formatElapsedTime(videolist[position].duration/1000))
                    .bold { append("\n\nFile Size: ") }.append(Formatter.formatShortFileSize(content,videolist[position].size.toLong()))
                    .bold { append("\n\nLocation: ") }.append(videolist[position].path)
                bindingIF.detailtv.text = infoText
                dialogIF.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(MaterialColors.getColor(content,com.google.android.material.R.attr.useMaterialThemeColors,
                    Color.TRANSPARENT))
            }
            bindingMF.delete.setOnClickListener {
                dialog.dismiss()
                listener?.delposition(position)

            }
        }
    }

    override fun getItemCount(): Int {
        return videolist.size
    }
    private fun sendintent(pos:Int,ref:String){
        PlayerActivity.position = pos
        val intent= Intent(content, PlayerActivity::class.java)
        intent.putExtra("class",ref)
        ContextCompat.startActivity(content,intent,null)
    }
     fun updateList(searchList:ArrayList<video>){
        videolist = ArrayList()
        videolist.addAll(searchList)
        notifyDataSetChanged()
    }
    private fun requestPermissionR(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if(!Environment.isExternalStorageManager()){
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data= Uri.parse("package:${content.applicationContext.packageName}")
                    ContextCompat.startActivity(content,intent,null)
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
            val pi = MediaStore.createDeleteRequest(content.contentResolver, uriList)
            (content as Activity).startIntentSenderForResult(
                pi.intentSender, 125,
                null, 0, 0, 0, null
            )
        } else {
            //for devices less than android 11
            val file = File(Video.videoList[position].path)
            val builder = MaterialAlertDialogBuilder(content)
            builder.setTitle("Delete Video?")
                .setMessage(Video.videoList[position].title)
                .setPositiveButton("Yes") { self, _ ->
                    if (file.exists() && file.delete()) {
                        MediaScannerConnection.scanFile(content, arrayOf(file.path), null, null)
                        updateDeleteUI(position = position)
                    }
                    self.dismiss()
                }
                .setNegativeButton("No") { self, _ -> self.dismiss() }
            val delDialog = builder.create()
            delDialog.show()
        }
    }
    private fun updateDeleteUI(position: Int){
        when{
            Video.search -> {
                Video.datachanged = true
                Video.videoList.removeAt(position)
                notifyDataSetChanged()
            }
            isFolder -> {
                Video.datachanged = true
                folderActivity.currentfolderVideos.removeAt(position)
                notifyDataSetChanged()
            }
            else -> {
                Video.videoList.removeAt(position)
                notifyDataSetChanged()
            }
        }
    }
    fun requestWrite(){
        val uriList: List<Uri> = listOf(Uri.withAppendedPath(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            Video.videoList[newposition].id))

        //requesting file write permission for specific files
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val pi = MediaStore.createWriteRequest(content.contentResolver, uriList)
            (content as Activity).startIntentSenderForResult(pi.intentSender, 126,
                null, 0, 0, 0, null)
        }
       // else renameFunction(newposition)
    }
    private fun renameFunction(position: Int) {
        val customDialogRF = LayoutInflater.from(this.content).inflate(R.layout.rename_view,
            (content as Activity).findViewById(R.id.drawerlayoutam),false)
        val bindingRF = RenameViewBinding.bind(customDialogRF)
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            dialogRF = MaterialAlertDialogBuilder(this.content).setView(customDialogRF)
                .setCancelable(false)
                .setPositiveButton("Rename") { self, _ ->
                    val currentFile = File(videolist[position].path)
                    val newName = bindingRF.renameField.text
                    if (newName != null && currentFile.exists() && newName.toString().isNotEmpty()) {
                        val newFile = File(
                            currentFile.parentFile,
                            newName.toString() + "." + currentFile.extension
                        )
                        val fromUri = Uri.withAppendedPath(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            videolist[position].id
                        )
                        ContentValues().also {
                            it.put(MediaStore.Files.FileColumns.IS_PENDING, 1)
                            content.contentResolver.update(fromUri, it, null, null)
                            it.clear()

                            //updating file details
                            it.put(MediaStore.Files.FileColumns.DISPLAY_NAME, newName.toString())
                            it.put(MediaStore.Files.FileColumns.IS_PENDING, 0)
                            content.contentResolver.update(fromUri, it, null, null)
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
            dialogRF = MaterialAlertDialogBuilder(content).setView(customDialogRF)
                .setCancelable(false)
                .setPositiveButton("Rename"){ self, _ ->
                    val currentFile = File(Video.videoList[position].path)
                    val newName = bindingRF.renameField.text
                    if(newName != null && currentFile.exists() && newName.toString().isNotEmpty()){
                        val newFile = File(currentFile.parentFile, newName.toString()+"."+currentFile.extension)
                        if(currentFile.renameTo(newFile)){
                            MediaScannerConnection.scanFile(content, arrayOf(newFile.toString()), arrayOf("video/*"), null)
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
        bindingRF.renameField.text = SpannableStringBuilder(videolist[position].title)
        dialogRF.show()
        dialogRF.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(MaterialColors.getColor(content,com.google.android.material.R.attr.useMaterialThemeColors,
            Color.TRANSPARENT))
        dialogRF.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(MaterialColors.getColor(content,com.google.android.material.R.attr.useMaterialThemeColors,
            Color.TRANSPARENT))
    }
    private fun updateRenameUI(position: Int, newName: String, newFile: File){
        when{
            Video.search -> {
                Video.searchList[position].title = newName
                Video.searchList[position].path = newFile.path
                Video.searchList[position].artUri = Uri.fromFile(newFile)
                notifyItemChanged(position)
            }
            isFolder -> {
                folderActivity.currentfolderVideos[position].title = newName
                folderActivity.currentfolderVideos[position].path = newFile.path
                folderActivity.currentfolderVideos[position].artUri = Uri.fromFile(newFile)
                notifyItemChanged(position)
                Video.datachanged = true
            }
            else -> {
                Video.videoList[position].title = newName
                Video.videoList[position].path = newFile.path
                Video.videoList[position].artUri = Uri.fromFile(newFile)
                notifyItemChanged(position)
            }
        }
    }
}
