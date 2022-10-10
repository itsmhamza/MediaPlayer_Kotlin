package com.example.mediaplayer.fragments.Music

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.MainActivity
import com.example.mediaplayer.MusicActivity
import com.example.mediaplayer.R
import com.example.mediaplayer.Themes.Themes
import com.example.mediaplayer.databinding.ActivityPlaylistDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

class playlist_details : AppCompatActivity() {
    lateinit var binding: ActivityPlaylistDetailsBinding
    lateinit var adopter: MusicAdopter
    companion object{
        var currentplaylistpos:Int =-1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val pref = this.getSharedPreferences("smartbar", Context.MODE_PRIVATE)
        MainActivity.currenttheme = pref?.getString("theme", "0").toString()
        setthemeActivity()
        currentplaylistpos = intent.extras?.get("index") as Int
        try {
            playlists.musicPlaylist.ref[currentplaylistpos].playlist=
                checkdata(playlist = playlists.musicPlaylist.ref[currentplaylistpos].playlist)
        }catch (e:Exception){}

        binding.playlistdRecyclerview.setItemViewCacheSize(13)
        binding.playlistdRecyclerview.setHasFixedSize(true)
        binding.playlistdRecyclerview.layoutManager = LinearLayoutManager(this)
        adopter = MusicAdopter(this,playlists.musicPlaylist.ref[currentplaylistpos].playlist,playlistdetails = true)
        binding.playlistdRecyclerview.adapter = adopter
        binding.backPlay.setOnClickListener {
            finish()
        }
        binding.shufflePlaylistd.setOnClickListener {
            val intent = Intent(this, MusicActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "playlistdetailsShuffle")
            startActivity(intent)
        }
        binding.addPlaylistd.setOnClickListener {
            startActivity(Intent(this,selectionActivity::class.java))
            binding.deltePlaylistd.visibility = View.VISIBLE
        }
        binding.deltePlaylistd.setOnClickListener {
            if (playlists.musicPlaylist.ref[currentplaylistpos].playlist.isNotEmpty()) {
                val builder = MaterialAlertDialogBuilder(this)
                    .setTitle("Remove")
                    .setMessage("Do you want to Removed all songs from playlists?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        playlists.musicPlaylist.ref[currentplaylistpos].playlist.clear()
                        adopter.refreshplaylist()
                        binding.shufflePlaylistd.visibility = View.INVISIBLE
                        binding.deltePlaylistd.visibility = View.INVISIBLE
                        binding.moreInfo.text = "  Total ${0} Songs \n\n" +
                                "  Created On: \n  ${ playlists.musicPlaylist.ref[currentplaylistpos].createdOn} \n\n  "+
                                "Created By: \n   ${ playlists.musicPlaylist.ref[currentplaylistpos].createdBy}  "
                        dialog.dismiss()

                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                val customDialog = builder.create()
                customDialog.show()
            }
        }

    }
    fun setthemeActivity() {
        val pref = this.getSharedPreferences("smartbar", Context.MODE_PRIVATE)
        Themes.currenttheme =pref?.getString("theme","0").toString()
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

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding.playlistdName.text = playlists.musicPlaylist.ref[currentplaylistpos].name
        binding.moreInfo.text = "  Total ${adopter.itemCount} Songs \n\n" +
                "  Created On: \n  ${ playlists.musicPlaylist.ref[currentplaylistpos].createdOn} \n\n  "+
                "Created By: \n   ${ playlists.musicPlaylist.ref[currentplaylistpos].createdBy}  "
        if (adopter.itemCount>0){
           /* Glide.with(this)
                .load(playlists.musicPlaylist.ref[currentplaylistpos].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.ic_music_selection).centerCrop())
                .into(binding.playlistdImg)*/
            binding.shufflePlaylistd.visibility = View.VISIBLE
        }
        adopter.notifyDataSetChanged()
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonStringplaylist= GsonBuilder().create().toJson(playlists.musicPlaylist)
        editor.putString("musicplaylist",jsonStringplaylist)
        editor.apply()

    }
}