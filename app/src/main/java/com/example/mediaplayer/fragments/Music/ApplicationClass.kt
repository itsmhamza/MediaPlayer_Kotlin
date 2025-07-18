package com.example.mediaplayer.fragments.Music

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log

class ApplicationClass: Application() {
    companion object{
        const val channelId="channel1"
        const val PLAY="play"
        const val NEXT="next"
        const val PREVIOUS="previous"
        const val EXIT="exit"
    }
    override fun onCreate() {
        super.onCreate()
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(channelId,"Now Playing Song",NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "Showing songs"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}