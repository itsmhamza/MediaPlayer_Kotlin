package com.example.mediaplayer

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdRevenueListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
open class NativeAd:AppCompatActivity(),MaxAdRevenueListener {


    private var nativeAdLoader: MaxNativeAdLoader? = null
    private var loadedNativeAd: MaxAd? = null
    private lateinit var nativeAdView: MaxNativeAdView
    // AD Mob google Large ads
    fun ADmodADs(
        ctx : Context,
        context:Activity,
        shimmerlayout: ShimmerFrameLayout,
        nativeAdLayout: FrameLayout
    ) {
        nativeAdLayout.visibility = View.GONE
        shimmerlayout.visibility = View.VISIBLE
        shimmerlayout.startShimmer()
        val builder = AdLoader.Builder(context,context.getString(R.string.native_id))
            .forNativeAd {
                val adView = LayoutInflater.from(context).inflate(R.layout.nativead,null) as NativeAdView
                if (adView!=null){
                    populateUnifiedNativeAdView(it, adView)
                }
                nativeAdLayout.removeAllViews()
                nativeAdLayout.addView(adView)
            }
        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()
        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()
        builder.withNativeAdOptions(adOptions)
        val adLoader =builder.withAdListener(object : AdListener(){

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
              loadApplovinNativeAds(ctx, context,shimmerlayout,nativeAdLayout)
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                nativeAdLayout.visibility = View.VISIBLE
                shimmerlayout.visibility = View.GONE
                shimmerlayout.startShimmer()
            }
        }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateUnifiedNativeAdView(
        nativeAd: com.google.android.gms.ads.nativead.NativeAd,
        adView: NativeAdView,
    ) {
            val mediaView =
                adView.findViewById(R.id.ad_media12) as com.google.android.gms.ads.nativead.MediaView
                adView.mediaView = mediaView
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline))
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action))
            adView.setIconView(adView.findViewById(R.id.ad_app_icon))
            adView.setStarRatingView(adView.findViewById(R.id.ad_rating))
            (adView.headlineView as TextView).text = nativeAd.headline
            if (nativeAd.callToAction == null) {
                adView.callToActionView.visibility = View.INVISIBLE
            } else {
                adView.callToActionView.visibility = View.VISIBLE
                (adView.callToActionView as Button).text = nativeAd.callToAction
            }
            if (adView.starRatingView != null) {
                (adView.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
            }
            if (nativeAd.icon == null) {
                adView.iconView.visibility = View.GONE
            } else {
                (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon.drawable
                )
                adView.iconView.visibility = View.VISIBLE
            }
            adView.setNativeAd(nativeAd)

    }


    //Ad Mob Google small ads
    fun ADmodADsSmall(
        c: Context,
        context: Activity,
        shimmerlayout: ShimmerFrameLayout,
        nativeAdLayout: FrameLayout, )
    {
        nativeAdLayout.visibility = View.GONE
        shimmerlayout.visibility = View.VISIBLE
        shimmerlayout.startShimmer()

        val builder = AdLoader.Builder(context,context.getString(R.string.native_id))
            .forNativeAd {
                val adView = LayoutInflater.from(context).inflate(R.layout.nativead_s,null) as NativeAdView
                if (adView!=null){
                    populateUnifiedNativeAdViewSmall(it, adView)
                }
                nativeAdLayout.removeAllViews()
                nativeAdLayout.addView(adView)
            }
        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()
        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()
        builder.withNativeAdOptions(adOptions)
        val adLoader =builder.withAdListener(object : AdListener(){

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
               loadApplovinNativeAdsSmall(c, context,shimmerlayout,nativeAdLayout)
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                nativeAdLayout.visibility = View.VISIBLE
                shimmerlayout.visibility = View.GONE
                shimmerlayout.stopShimmer()
            }
        }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateUnifiedNativeAdViewSmall(
        nativeAd: com.google.android.gms.ads.nativead.NativeAd,
        adView: NativeAdView,
    ) {
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline))
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action))
        adView.setIconView(adView.findViewById(R.id.ad_app_icon))
        adView.setStarRatingView(adView.findViewById(R.id.ad_rating))
        (adView.headlineView as TextView).text = nativeAd.headline
        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        if (adView.starRatingView != null) {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
        }
        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon.drawable
            )
            adView.iconView.visibility = View.VISIBLE
        }
        adView.setNativeAd(nativeAd)

    }


    //Applovin ad
    open fun loadApplovinNativeAds(
        ctx:Context,
        context:Activity,
        shimmerlayout: ShimmerFrameLayout,
        nativeAdLayout: FrameLayout
    ){
        nativeAdLoader = MaxNativeAdLoader("068ecff4f7dd728b", ctx)
        nativeAdLoader!!.setRevenueListener { ad: MaxAd? -> }
        nativeAdLoader!!.setNativeAdListener(object : MaxNativeAdListener() {
            override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, nativeAd: MaxAd?) {
                super.onNativeAdLoaded(nativeAdView, nativeAd)
                // Clean up any pre-existing native ad to prevent memory leaks.
                //if (inactivity.isDestroyed) {
//              if (loadedNativeAd != null) {
//              nativeAdLoader!!.destroy(loadedNativeAd)
//              }
                // Save ad for cleanup.
                loadedNativeAd = nativeAd
                /* frameLayout2.removeAllViews()
                frameLayout2.addView(nativeAdView)
                shimmerFrameLayout.visibility = View.GONE
                parentview.visibility=View.VISIBLE*/
                nativeAdLayout.visibility = View.VISIBLE
                shimmerlayout.visibility = View.INVISIBLE
                // Add ad view to view.
                nativeAdLayout.removeAllViews()
                nativeAdLayout.addView(nativeAdView)
                //}
            }
            override fun onNativeAdLoadFailed(s: String, maxError: MaxError) {
                super.onNativeAdLoadFailed(s, maxError)
                /*frameLayout2.visibility = View.GONE
                shimmerFrameLayout.visibility = View.GONE
                parentview.visibility=View.GONE */
                shimmerlayout.visibility = View.VISIBLE
            }
            override fun onNativeAdClicked(maxAd: MaxAd) {
                super.onNativeAdClicked(maxAd)
            }
        })
        nativeAdLoader!!.loadAd(createNativeAdView(context))
    }
    open fun createNativeAdView(activity: Activity): MaxNativeAdView? {
        val binder: MaxNativeAdViewBinder =
            MaxNativeAdViewBinder.Builder(R.layout.applovin_native)
                .setTitleTextViewId(R.id.title_text_view)
                .setBodyTextViewId(R.id.body_text_view)
                .setAdvertiserTextViewId(R.id.advertiser_textView)
                .setIconImageViewId(R.id.icon_image_view)
                .setMediaContentViewGroupId(R.id.media_view_container)
                .setOptionsContentViewGroupId(R.id.options_view)
                .setCallToActionButtonId(R.id.cta_button)
                .build();
        return MaxNativeAdView(binder, activity)
    }









    //applovin ad small
    open fun loadApplovinNativeAdsSmall(
        c:Context,
        context:Activity,
        shimmerlayout: ShimmerFrameLayout,
        nativeAdLayout: FrameLayout
    ){
        nativeAdLoader = MaxNativeAdLoader("068ecff4f7dd728b", c)
        nativeAdLoader!!.setRevenueListener { ad: MaxAd? -> }
        nativeAdLoader!!.setNativeAdListener(object : MaxNativeAdListener() {
            override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, nativeAd: MaxAd?) {
                super.onNativeAdLoaded(nativeAdView, nativeAd)
                // Clean up any pre-existing native ad to prevent memory leaks.
                //if (inactivity.isDestroyed) {
//              if (loadedNativeAd != null) {
//              nativeAdLoader!!.destroy(loadedNativeAd)
//              }
                // Save ad for cleanup.
                loadedNativeAd = nativeAd
                /* frameLayout2.removeAllViews()
                frameLayout2.addView(nativeAdView)
                shimmerFrameLayout.visibility = View.GONE
                parentview.visibility=View.VISIBLE*/
                nativeAdLayout.visibility = View.VISIBLE
                shimmerlayout.visibility = View.INVISIBLE
                // Add ad view to view.
                nativeAdLayout.removeAllViews()
                nativeAdLayout.addView(nativeAdView)
                //}
            }
            override fun onNativeAdLoadFailed(s: String, maxError: MaxError) {
                super.onNativeAdLoadFailed(s, maxError)
                /*frameLayout2.visibility = View.GONE
                shimmerFrameLayout.visibility = View.GONE
                parentview.visibility=View.GONE */
                shimmerlayout.visibility = View.VISIBLE
            }
            override fun onNativeAdClicked(maxAd: MaxAd) {
                super.onNativeAdClicked(maxAd)
            }
        })
        nativeAdLoader!!.loadAd(createNativeAdViewSmall(context))
    }
    open fun createNativeAdViewSmall(activity: Activity): MaxNativeAdView? {
        val binder: MaxNativeAdViewBinder =
            MaxNativeAdViewBinder.Builder(R.layout.applovin_native_s)
                .setTitleTextViewId(R.id.title_text_view_s)
                .setAdvertiserTextViewId(R.id.advertiser_textView_s)
                .setIconImageViewId(R.id.icon_image_view_s)
                .setOptionsContentViewGroupId(R.id.ad_options_view_s)
                .setCallToActionButtonId(R.id.cta_button_s)
                .build()
        return MaxNativeAdView(binder, activity)
    }

    override fun onAdRevenuePaid(ad: MaxAd?) {
        TODO("Not yet implemented")
    }
}