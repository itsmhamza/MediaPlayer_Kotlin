package com.example.mediaplayer.fragments.Music

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.MainActivity
import com.example.mediaplayer.R
import com.example.mediaplayer.Themes.Themes
import com.example.mediaplayer.databinding.ActivitySelectionBinding

class selectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectionBinding
    lateinit var adopter: MusicAdopter

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MediaPlayer)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val pref = this.getSharedPreferences("smartbar", Context.MODE_PRIVATE)
        MainActivity.currenttheme = pref?.getString("theme", "0").toString()
        setthemeActivity()
        binding.selectionRecyclerview.setItemViewCacheSize(30)
        binding.selectionRecyclerview.setHasFixedSize(true)
        binding.selectionRecyclerview.layoutManager = LinearLayoutManager(this)
        adopter = MusicAdopter(this, allsongs.MusicListMA, selectionActivity = true)
        binding.selectionRecyclerview.adapter = adopter
        binding.backSelection.setOnClickListener {
            finish()
        }
        binding.searchSelection.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                allsongs.musiclistSearch = ArrayList()
                if (newText != null) {
                    val userInput = newText.lowercase()
                    for (songs in allsongs.MusicListMA)
                        if (songs.stitle.lowercase().contains(userInput))
                            allsongs.musiclistSearch.add(songs)
                    allsongs.search = true
                    adopter.updatemusicList(searchList = allsongs.musiclistSearch)
                }
                return true
            }
        })
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

}