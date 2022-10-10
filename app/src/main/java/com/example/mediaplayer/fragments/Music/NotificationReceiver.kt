package com.example.mediaplayer.fragments.Music

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mediaplayer.MusicActivity
import com.example.mediaplayer.R

class NotificationReceiver:BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            ApplicationClass.PREVIOUS -> prenextsong(increment = false,context=context!!)
            ApplicationClass.PLAY -> if (MusicActivity.isplaying) pausemusic()  else playmusic()
            ApplicationClass.NEXT -> prenextsong(increment = true,context=context!!)
            ApplicationClass.EXIT ->
           {
               exit()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.S)
    private fun playmusic(){
        MusicActivity.isplaying = true
        MusicActivity.musicService!!.mediaPlayer!!.start()
        MusicActivity.musicService!!.showNotification(R.drawable.ic_play_b)
        MusicActivity.binding.playPause.setImageResource(R.drawable.ic_play_b)
        NowPlaying.binding.playpausePlaying.setImageResource(R.drawable.ic_baseline_pause_24)
    }
    @RequiresApi(Build.VERSION_CODES.S)
    private fun pausemusic(){
        MusicActivity.isplaying = false
        MusicActivity.musicService!!.mediaPlayer!!.pause()
        MusicActivity.musicService!!.showNotification(R.drawable.ic_pause_b)
        MusicActivity.binding.playPause.setImageResource(R.drawable.ic_pause_b)
        NowPlaying.binding.playpausePlaying.setImageResource(R.drawable.ic_baseline_play_arrow_24)
    }
    @RequiresApi(Build.VERSION_CODES.S)
    private fun prenextsong(increment:Boolean, context: Context){
        setsongposition(increment =increment)
        MusicActivity.musicService!!.createmediaplayer()
        Glide.with(context)
            .load(MusicActivity.musicListPA[MusicActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_music_selection).centerCrop())
            .into(MusicActivity.binding.songImg)
        MusicActivity.binding.songName.text = MusicActivity.musicListPA[MusicActivity.songPosition].stitle
        Glide.with(context)
            .load(MusicActivity.musicListPA[MusicActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_music_selection).centerCrop())
            .into(NowPlaying.binding.songImg)
        NowPlaying.binding.songNowplay.text = MusicActivity.musicListPA[MusicActivity.songPosition].stitle
        playmusic()
        MusicActivity.findex = favrtchecker(MusicActivity.musicListPA[MusicActivity.songPosition].id)
        if (MusicActivity.isFavt) MusicActivity.binding.favt.setImageResource(R.drawable.ic_baseline_favorite_24)
        else MusicActivity.binding.favt.setImageResource(R.drawable.ic_favourite_b)
    }
}