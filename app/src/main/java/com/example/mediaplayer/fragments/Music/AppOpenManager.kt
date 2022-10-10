package com.example.mediaplayer.fragments.Music

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.mediaplayer.splashActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

class AppOpenManager(private val myApplication: ApplicationClass) :
    Application.ActivityLifecycleCallbacks,
    LifecycleObserver {

    private var appOpenAd: AppOpenAd? = null
    private var loadCallback: AppOpenAd.AppOpenAdLoadCallback? = null
    private var currentActivity: Activity? = null
    private var isShowingAd = false
    private val adRequest: AdRequest
        get() = AdRequest.Builder().build()
    val isAdAvailable: Boolean
        get() = appOpenAd != null

    companion object {
       // private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/3419835294" //test id
        private const val AD_UNIT_ID = "ca-app-pub-6705009758927743/9777177233" //id
    }

    init {
        myApplication.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun fetchAd(){
        if(isAdAvailable){
            return
        }
        loadCallback = object :AppOpenAd.AppOpenAdLoadCallback(){

            override fun onAdLoaded(p0: AppOpenAd) {
                super.onAdLoaded(p0)
                appOpenAd=p0
            }
            override fun onAdFailedToLoad(p0: LoadAdError) {
                Log.d("tag", "onAppOpenAdFailedToLoad: ")
            }
        }
        val request = adRequest
        AppOpenAd.load(myApplication,
            AD_UNIT_ID, request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback)
    }

    fun showAdIfAvailable(){
        if(!isShowingAd && isAdAvailable && splashActivity.ads==true){
            Log.d("tag", "will show ad ")
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback(){
                    override fun onAdDismissedFullScreenContent() {
                        appOpenAd = null
                        isShowingAd = false
                        fetchAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError?) {
                    }

                    override fun onAdShowedFullScreenContent() {
                        isShowingAd = true
                    }
                }
            appOpenAd!!.show(currentActivity)
        } else{
            Log.d("tag", "can't show ad ")
                fetchAd()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart(){
        showAdIfAvailable()
        Log.d("tag", "onStart: ")
    }
}