package com.example.mediaplayer.fragments.Music

import android.content.Intent

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mediaplayer.MusicActivity
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentNowPlayingBinding

class NowPlaying : Fragment() {
    companion object{
        lateinit var binding:FragmentNowPlayingBinding
    }
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val view =inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE
        binding.playpausePlaying.setOnClickListener {
            if (MusicActivity.isplaying) {
                pausemusic()
                binding.playpausePlaying.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
            else playmusic()
        }
        binding.nextSongplaying.setOnClickListener {
            setsongposition(increment =true)
            MusicActivity.musicService!!.createmediaplayer()
            Glide.with(this)
                .load(MusicActivity.musicListPA[MusicActivity.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.ic_music_selection_cir).centerCrop())
                .into(binding.songImg)
            binding.songNowplay.text = MusicActivity.musicListPA[MusicActivity.songPosition].stitle
            MusicActivity.musicService!!.showNotification(R.drawable.ic_baseline_pause_24)
            playmusic()
        }
        binding.root.setOnClickListener {
            val intent = Intent(requireContext(),MusicActivity::class.java)
            intent.putExtra("index",MusicActivity.songPosition)
            intent.putExtra("class","NowPlaying")
            ContextCompat.startActivity(requireContext()   ,intent,null)
        }
        return view
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onResume() {
        super.onResume()
        if (MusicActivity.musicService!=null){
            binding.root.visibility=View.VISIBLE
            binding.songNowplay.isSelected =  true
            Glide.with(this)
                .load(MusicActivity.musicListPA[MusicActivity.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.ic_music_selection_cir).centerCrop())
                .into(binding.songImg)
            binding.songNowplay.text = MusicActivity.musicListPA[MusicActivity.songPosition].stitle
            if(MusicActivity.isplaying) binding.playpausePlaying.setImageResource(R.drawable.ic_baseline_pause_24)
            else binding.playpausePlaying.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun playmusic(){
        MusicActivity.musicService!!.mediaPlayer!!.start()
        binding.playpausePlaying.setImageResource(R.drawable.ic_baseline_pause_24)
        MusicActivity.musicService!!.showNotification(R.drawable.ic_play_b)
        MusicActivity.isplaying = true
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun pausemusic(){
        MusicActivity.musicService!!.mediaPlayer!!.pause()
        binding.playpausePlaying.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        MusicActivity.musicService!!.showNotification(R.drawable.ic_pause_b)
        MusicActivity.isplaying = false
    }

}