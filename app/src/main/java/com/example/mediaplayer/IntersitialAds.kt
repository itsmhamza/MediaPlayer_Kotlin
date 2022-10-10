package com.example.mediaplayer

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.utils.InterstialAppLovinAd


class InterstialAdsCall {
    var mInterstitialAd: InterstitialAd? = null
    /*var admobads: Boolean? = false
        private set*/

    //singleton pattern used
    companion object {
        @Volatile
        private var instance: InterstialAdsCall? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: InterstialAdsCall().also { instance = it }
        }
    }
    fun loadInterstitialAd(context: Context) {
        context.let {
            InterstitialAd.load(
                it, it.getString(R.string.intersitial_id),
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(ad: LoadAdError) {
                        // OpenApp.isInterstitialShown = false
                        // loadInterstitialAd(context)
                        Log.e("InterstiatialFail", "Reloaded___")
                        //Applovin adds call
                        InterstialAppLovinAd.getInstance().LoadMaxInterstitialAd(context as Activity)
                    }
                    override fun onAdLoaded(ad: InterstitialAd) {
                        mInterstitialAd = ad
                        mInterstitialAd?.fullScreenContentCallback =
                            object : FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {
                                    //OpenApp.isInterstitialShown = false
                                   // admobads = true
                                    mInterstitialAd = null
                                    loadInterstitialAd(context)
                                    Log.e("InterstiatialReload", "Reloaded___")
                                }
                                override fun onAdFailedToShowFullScreenContent(p0: AdError?) {
                                    // OpenApp.isInterstitialShown = false
                                    super.onAdFailedToShowFullScreenContent(p0)
                                }
                                override fun onAdShowedFullScreenContent() {
                                    // OpenApp.isInterstitialShown = true
                                    super.onAdShowedFullScreenContent()
                                }
                            }
                        Log.e("Interstitial____", "AdLoaded____")
                    }
                })
        }
    }
    // to show Interstitial Ad Activity reference must be given
    fun showInterstitialAdNew(activity: Activity) {
        if (mInterstitialAd != null) {
            activity.let {
                mInterstitialAd?.show(it)
            }
        }
        else {
            InterstialAppLovinAd.getInstance().showInterstitialAdLovin()
        }
    }
}