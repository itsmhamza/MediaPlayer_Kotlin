package com.example.mediaplayer.fragments.Music

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.mediaplayer.MusicActivity
import com.example.mediaplayer.R

class MusicService: Service(),AudioManager.OnAudioFocusChangeListener {

    private var myBinder=MyBinder()
    var mediaPlayer:MediaPlayer?=null
    private lateinit var mediaSession:MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager
    override fun onBind(intent: Intent?): IBinder? {
        mediaSession = MediaSessionCompat(baseContext,"My Music")
        return myBinder
    }
    inner class MyBinder:Binder(){
        fun currentServices():MusicService{
            return this@MusicService
        }
    }
    @RequiresApi(Build.VERSION_CODES.S)
    fun showNotification(playpause:Int) {
//        val intent = Intent(baseContext, MainActivity::class.java)
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        val contextintent = PendingIntent.getActivity(baseContext,0,intent,PendingIntent.FLAG_MUTABLE)

        val prevInt = Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext,0,prevInt,PendingIntent.FLAG_MUTABLE)

        val playInt = Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext,0,playInt,PendingIntent.FLAG_MUTABLE)

        val nextInt = Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext,0,nextInt,PendingIntent.FLAG_MUTABLE)

        val exitInt = Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext,0,exitInt,PendingIntent.FLAG_MUTABLE)
        val imgart = getimgart(MusicActivity.musicListPA[MusicActivity.songPosition].path)
        val img = if (imgart != null)
        {
            BitmapFactory.decodeByteArray(imgart,0,imgart.size)
        }
        else
            {
            BitmapFactory.decodeResource(resources,R.drawable.ic_vid)
        }
        val notification = NotificationCompat.Builder(baseContext,ApplicationClass.channelId)
//            .setContentIntent(contextintent)
            .setContentTitle(MusicActivity.musicListPA[MusicActivity.songPosition].stitle)
            .setContentText(MusicActivity.musicListPA[MusicActivity.songPosition].artist)
            .setSmallIcon(R.drawable.ic_music_selection)
            .setLargeIcon(img)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_reverse_b,"Previous",prevPendingIntent)
            .addAction(playpause,"Play",playPendingIntent)
            .addAction(R.drawable.ic_forward_b,"Next",nextPendingIntent)
            .addAction(R.drawable.ic_baseline_exit_to_app,"Exit",exitPendingIntent)
            .build()
        startForeground(13,notification)
    }
     @RequiresApi(Build.VERSION_CODES.S)
     fun createmediaplayer(){
        try {
            if (MusicActivity.musicService!!.mediaPlayer == null) mediaPlayer = MediaPlayer()
            MusicActivity.musicService!!.mediaPlayer!!.reset()
            MusicActivity.musicService!!.mediaPlayer!!.setDataSource(MusicActivity.musicListPA[MusicActivity.songPosition].path)
            MusicActivity.musicService!!.mediaPlayer!!.prepare()
            MusicActivity.binding.playPause.setImageResource(R.drawable.ic_play_b)
            MusicActivity.musicService!!.showNotification(R.drawable.ic_play_b)
            MusicActivity.binding.startSong.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            MusicActivity.binding.endSong.text = formatDuration(mediaPlayer!!.duration.toLong())
            MusicActivity.binding.seekbarSong.progress = 0
            MusicActivity.binding.seekbarSong.max = mediaPlayer!!.duration
            MusicActivity.nowplayingid = MusicActivity.musicListPA[MusicActivity.songPosition].id
        }
        catch (e:Exception)
        {
            return
        }
    }
    fun seekbarset() {
            runnable = Runnable {
                try {
                    MusicActivity.binding.startSong.text =
                        formatDuration(mediaPlayer!!.currentPosition.toLong())
                MusicActivity.binding.seekbarSong.progress = mediaPlayer!!.currentPosition
                Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
                }catch (e:Exception){}
            }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)
    }
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onAudioFocusChange(focusChange: Int) {
        if(focusChange<=0){
            MusicActivity.binding.playPause.setImageResource(R.drawable.ic_play_b)
            NowPlaying.binding.playpausePlaying.setImageResource(R.drawable.ic_play_b)
            showNotification(R.drawable.ic_play_b)
            MusicActivity.isplaying =true
            MusicActivity.musicService!!.mediaPlayer!!.start()
        }
//        else{
//            MusicActivity.binding.playPause.setImageResource(R.drawable.ic_play_b)
//            NowPlaying.binding.playpausePlaying.setImageResource(R.drawable.ic_play_b)
//            showNotification(R.drawable.ic_play_b)
//            MusicActivity.isplaying =true
//            MusicActivity.musicService!!.mediaPlayer!!.start()
//        }
    }
}