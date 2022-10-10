package com.example.mediaplayer.fragments.Video

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.format.DateUtils
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
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
import com.example.mediaplayer.databinding.VideoMoreFeaturesBinding
import com.example.mediaplayer.databinding.VideoView2Binding
import com.example.mediaplayer.interfaces.onItemRename
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class VideoRAdopter(private val content:Context,
                    private var videolist: ArrayList<video>, private var isFolder:Boolean=false, onItemRename: onItemRename)
    :RecyclerView.Adapter<VideoRAdopter.MyHolder>() {
    private var listener:onItemRename? = null
    private var newposition = 0
    companion object {
        var media:Boolean=false
    }
    init {
        listener=onItemRename
    }
    class MyHolder(binding: VideoView2Binding):RecyclerView.ViewHolder(binding.root) {

        val title = binding.videoName
        val folder = binding.folderName
        val image = binding.image
        val size = binding.size
        val more = binding.more
        val root =binding.root
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(VideoView2Binding.inflate(LayoutInflater.from(content), parent, false))

    }
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyHolder, @SuppressLint("RecyclerView") position: Int) {
        val status = content?.getSharedPreferences("media",Context.MODE_PRIVATE)
            ?.getBoolean("status",true)
        if(status!!) {
              holder.title.isSelected = true
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
        if (isFolder==false) {
            holder.more.visibility = View.VISIBLE
            holder.more.setOnClickListener {
                newposition = position
                val customDialog = LayoutInflater.from(this.content)
                    .inflate(R.layout.video_more_features, holder.root, false)
                val bindingMF = VideoMoreFeaturesBinding.bind(customDialog)
                val dialog = MaterialAlertDialogBuilder(this.content).setView(customDialog)
                    .create()
                dialog.show()
                bindingMF.rename.setOnClickListener {
                    dialog.dismiss()
                    listener?.position(newposition)

                }
                bindingMF.details.setOnClickListener {
                    dialog.dismiss()
                    val customDialogIF = LayoutInflater.from(this.content)
                        .inflate(R.layout.details_view, holder.root, false)
                    val bindingIF = DetailsViewBinding.bind(customDialogIF)
                    val dialogIF = MaterialAlertDialogBuilder(this.content).setView(customDialogIF)
                        .setCancelable(false)
                        .setPositiveButton("Ok") { self, _ ->
                            self.dismiss()
                        }
                        .create()
                    dialogIF.show()
                    val infoText = SpannableStringBuilder().bold { append("DETALS\n\nName: ") }
                        .append(videolist[position].title)
                        .bold { append("\n\nDuration: ") }
                        .append(DateUtils.formatElapsedTime(videolist[position].duration / 1000))
                        .bold { append("\n\nFile Size: ") }.append(
                            Formatter.formatShortFileSize(
                                content,
                                videolist[position].size.toLong()
                            )
                        )
                        .bold { append("\n\nLocation: ") }.append(videolist[position].path)
                    bindingIF.detailtv.text = infoText
                    dialogIF.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(
                        MaterialColors.getColor(
                            content, R.color.white,
                            Color.TRANSPARENT
                        )
                    )
                }
                bindingMF.delete.setOnClickListener {
                    //   requestPermissionR()
                    dialog.dismiss()
                    listener?.delposition(newposition)

                }
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
}
