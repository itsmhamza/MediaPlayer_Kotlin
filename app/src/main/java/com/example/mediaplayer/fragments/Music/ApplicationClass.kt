package com.example.mediaplayer.fragments.Music

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.applovin.sdk.AppLovinSdk
import com.google.android.gms.ads.MobileAds

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
        MobileAds.initialize(this){
            Log.d("tag", "MobileAds init ")
        }
        AppOpenManager(this)
        AppLovinSdk.getInstance( this ).mediationProvider = "max"
        AppLovinSdk.getInstance( this ).initializeSdk {

        }
      //  AppLovinSdk.getInstance(this).settings.testDeviceAdvertisingIds = arrayListOf("3150311b-06c8-41c1-adc2-21082038149e","3c31b5ab-efd1-488b-b18e-db4b116fcfd6")

    }
}