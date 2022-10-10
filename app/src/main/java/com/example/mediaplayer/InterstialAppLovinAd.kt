package com.utils

import android.app.Activity
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.example.mediaplayer.InterstialAdsCall
import java.util.concurrent.TimeUnit

class InterstialAppLovinAd: AppCompatActivity() , MaxAdViewAdListener {

    var intersadlovin: MaxInterstitialAd? = null
        private set
    private var retryAttempt = 0.0

    companion object {
        @Volatile
        private var instanceapplovin: InterstialAppLovinAd? = null
        fun getInstance() =
            instanceapplovin ?: synchronized(this) {
            instanceapplovin ?: InterstialAppLovinAd().also { instanceapplovin = it }
            }
    }
    override fun onAdLoaded(ad: MaxAd?) {
        retryAttempt = 0.0
    }

    override fun onAdDisplayed(ad: MaxAd?) {
    }

    override fun onAdHidden(ad: MaxAd?) {
        //intersadlovin?.loadAd()
        InterstialAdsCall.getInstance().loadInterstitialAd(this)

    }

    override fun onAdClicked(ad: MaxAd?) {
    }

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
        retryAttempt++
        val delayMillis = TimeUnit.SECONDS.toMillis( Math.pow( 2.0, Math.min( 6.0, retryAttempt ) ).toLong() )

        Handler().postDelayed( { intersadlovin!!.loadAd() }, delayMillis )
    }

    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
        intersadlovin?.loadAd()
    }

    override fun onAdExpanded(ad: MaxAd?) {
    }

    override fun onAdCollapsed(ad: MaxAd?) {
    }

    fun LoadMaxInterstitialAd(activity: Activity) {
        intersadlovin = MaxInterstitialAd("63807542cb29c24f", activity)
        intersadlovin?.setListener(this)
        intersadlovin?.loadAd()
    }

    // to show Interstitial Ad Activity reference must be given
    fun showInterstitialAdLovin() {

        if (intersadlovin!=null) {
            if (intersadlovin!!.isReady) {
                intersadlovin?.showAd()
            }
        }
    }


}