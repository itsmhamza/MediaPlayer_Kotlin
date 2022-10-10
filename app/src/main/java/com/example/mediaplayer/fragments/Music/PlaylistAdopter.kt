package com.example.mediaplayer.fragments.Music

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.PlaylistsViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class PlaylistAdopter(private val context: Context, private var playlistlist:ArrayList<playlist>):
    RecyclerView.Adapter<PlaylistAdopter.MyHolder>() {
    class MyHolder(binding : PlaylistsViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.playlistImg
        val name = binding.playlistName
        val delete = binding.playlistDelete
        val size = binding.playlistsize
        val root =binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(PlaylistsViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }
    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.size.text = "${playlistlist[position].playlist.size} Audios"
        holder.name.text = playlistlist[position].name
        holder.name.isSelected = true
        holder.delete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
                .setTitle(playlistlist[position].name)
                .setMessage("Do You Want to Delete Playlist?")
                .setPositiveButton("Yes"){dialog,_->
                    playlists.musicPlaylist.ref.removeAt(position)
                    refreshPlaylist()
                }
                .setNegativeButton("No"){dialog,_->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
        }
        holder.root.setOnClickListener {
            val intent = Intent(context,playlist_details::class.java)
            intent.putExtra("index",position)
            ContextCompat.startActivity(context,intent,null)
        }
        if (playlists.musicPlaylist.ref[position].playlist.size>0){
            Glide.with(context)
                .load(playlists.musicPlaylist.ref[position].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.ic_music_selection_cir).centerCrop())
        }
    }

    override fun getItemCount(): Int {
        return playlistlist.size
    }
    fun refreshPlaylist(){
        playlistlist = ArrayList()
        playlistlist.addAll(playlists.musicPlaylist.ref)
        notifyDataSetChanged()
    }

}