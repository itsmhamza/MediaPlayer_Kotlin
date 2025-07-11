package com.example.mediaplayer.fragments.Music

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.text.SpannableStringBuilder
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mediaplayer.MusicActivity
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.AudioMoreFeaturesBinding
import com.example.mediaplayer.databinding.DetailsViewBinding
import com.example.mediaplayer.databinding.MusicViewBinding
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MusicAdopter(private val context: Context,private var musicList:ArrayList<Musics>,
                   private val playlistdetails:Boolean=false,
val selectionActivity:Boolean=false):RecyclerView.Adapter<MusicAdopter.MyHolder>() {
    class MyHolder(binding : MusicViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val stitle = binding.songName
        val album = binding.songAlbum
        val artist = binding.songArtist
        val image = binding.imagea
        val more = binding.more
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdopter.MyHolder {
        return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: MusicAdopter.MyHolder, position: Int) {
        val status = context?.getSharedPreferences("media",Context.MODE_PRIVATE)
            ?.getBoolean("status",true)
        if(status!!) {
            //  media=true
            holder.stitle.text = musicList[position].stitle
        }
        holder.artist.text = musicList[position].artist
        holder.album.text = musicList[position].album
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_music_selection_cir).centerCrop())
            .into(holder.image)
        holder.more.setOnClickListener {
            val customDialog = LayoutInflater.from(this.context).inflate(R.layout.audio_more_features,holder.root,false)
            val bindingMF = AudioMoreFeaturesBinding.bind(customDialog)
            val dialog = MaterialAlertDialogBuilder(this.context).setView(customDialog)
                .create()
            dialog.show()
            bindingMF.details.setOnClickListener {
                dialog.dismiss()
                val customDialogIF = LayoutInflater.from(this.context).inflate(R.layout.details_view,holder.root,false)
                val bindingIF = DetailsViewBinding.bind(customDialogIF)
                val dialogIF = MaterialAlertDialogBuilder(this.context).setView(customDialogIF)
                    .setCancelable(false)
                    .setPositiveButton("Ok"){
                            self,_->
                        self.dismiss()
                    }
                    .create()
                dialogIF.show()
                val infoText = SpannableStringBuilder().bold { append("DETAILS\n\nName: ") }.append(musicList[position].stitle)
                    .bold { append("\n\nDuration: ") }.append(DateUtils.formatElapsedTime(musicList[position].duration/1000))
                    .bold { append("\n\nAlbum: ") }.append(musicList[position].album)
                    .bold { append("\n\nArtist: ") }.append(musicList[position].artist)
                    .bold { append("\n\nLocation: ") }.append(musicList[position].path)
                bindingIF.detailtv.text = infoText
                dialogIF.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(
                    MaterialColors.getColor(context,com.google.android.material.R.attr.useMaterialThemeColors,
                    Color.BLACK))
            }

        }
        when {
            playlistdetails -> {
                holder.root.setOnClickListener {
                    sendintent(
                        ref = "playlistDetailsAdopter",
                        position = position
                    )
                }
            }
            selectionActivity ->{
                holder.root.setOnClickListener {
                       if (addSong(musicList[position])) {
                           holder.root.setBackgroundColor(
                               ContextCompat.getColor(
                                   context,
                                   R.color.selection_color
                               )
                           )
                           Handler().postDelayed({
                               holder.root.setBackgroundColor(
                                   MaterialColors.getColor(context,com.google.android.material.R.attr.useMaterialThemeColors,
                                       Color.TRANSPARENT))
                           },500)
                           Toast.makeText(context, "Added to Playlist", Toast.LENGTH_SHORT).show()
                       }
                       else
                       {
                           holder.root.setBackgroundColor(
                               ContextCompat.getColor(
                                   context,
                                   R.color.select_icon
                               )
                           )
                           Handler().postDelayed({
                               holder.root.setBackgroundColor(
                                   MaterialColors.getColor(context,com.google.android.material.R.attr.useMaterialThemeColors,
                                       Color.TRANSPARENT))
                           },500)
                           Toast.makeText(context, "Removed from Playlist", Toast.LENGTH_SHORT).show()
                       }
                }
            }

            else -> {
                holder.root.setOnClickListener {
                    when {
                        allsongs.search -> sendintent(
                            ref = "MusicAdopterSearch",
                            position = position
                        )
                        musicList[position].id == MusicActivity.nowplayingid ->
                            sendintent(ref = "NowPlaying", position = MusicActivity.songPosition)
                        else -> sendintent("MusicAdopter", position = position)
                    }
                }
            }
        }
    }

    private fun addSong(song: Musics): Boolean {
        playlists.musicPlaylist.ref[playlist_details.currentplaylistpos].playlist.forEachIndexed { index, musics ->
            if(song.id == musics.id){
                playlists.musicPlaylist.ref[playlist_details.currentplaylistpos].playlist.removeAt(index)
                return false
            }
        }
        playlists.musicPlaylist.ref[playlist_details.currentplaylistpos].playlist.add(song)
        return true
    }

    override fun getItemCount(): Int {
        return musicList.size
    }
    fun updatemusicList(searchList:ArrayList<Musics>){
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }
    private fun sendintent(ref:String,position: Int){
        val intent = Intent(context,MusicActivity::class.java)
        intent.putExtra("index",position)
        intent.putExtra("class",ref)
        ContextCompat.startActivity(context,intent,null)
    }
    fun refreshplaylist(){
        musicList = ArrayList()
        musicList = playlists.musicPlaylist.ref[playlist_details.currentplaylistpos].playlist
        notifyDataSetChanged()
    }
  /*  private fun requestPermissionR(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if(!Environment.isExternalStorageManager()){
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data= Uri.parse("package:${context.applicationContext.packageName}")
                ContextCompat.startActivity(context,intent,null)
            }
        }
    }*/
}