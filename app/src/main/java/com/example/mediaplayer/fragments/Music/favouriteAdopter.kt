package com.example.mediaplayer.fragments.Music

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mediaplayer.MusicActivity
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FavtViewBinding

class favouriteAdopter(private val context: Context, private var musicList:ArrayList<Musics>):
    RecyclerView.Adapter<favouriteAdopter.MyHolder>() {
    class MyHolder(binding : FavtViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.favtImg
        val songname = binding.favtName
        val root =binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): favouriteAdopter.MyHolder {
        return MyHolder(FavtViewBinding.inflate(LayoutInflater.from(context),parent,false))

    }
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.songname.text = musicList[position].stitle
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_music_selection_cir).centerCrop())
            .into(holder.image)
        holder.root.setOnClickListener {
                val intent = Intent(context, MusicActivity::class.java)
                intent.putExtra("index", position)
                intent.putExtra("class", "FavrtAdopter")
                ContextCompat.startActivity(context, intent, null)
            }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }
    fun updatefavourite(newList:ArrayList<Musics>){
        musicList= ArrayList()
        musicList.addAll(newList)
        notifyDataSetChanged()
    }

}