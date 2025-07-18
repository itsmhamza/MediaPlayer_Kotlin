-keep class androidx.multidex.** { *; }
-keep class androidx.** { 
*** startActivityForResult(***);
*** startActivity(***);
}
-keep class android.support.multidex.** { *; }
-keep class android.support.v4.app.** { *; }
-keep class io.fabric.sdk.android.** { *; }
-keep class okio.** { *; }
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class com.squareup.okhttp.** { *; }
-keep class com.android.volley.** { *; }
-keep class com.flurry.** { *; }
-keep class org.apache.** { *; }
-keep class com.applovin.** { *; }
-keep class com.google.android.gms.ads.** { *; }
-keep class com.ironsource.** { *; }
-keep class com.fyber.inneractive.** { *; }
-keep class com.vungle.** { *; }
-keep class com.unity3d.ads.** { *; }
-keep class com.unity3d.services.** { *; }
-keep class com.mintegral.msdk.** { *; }
-keep class com.mbridge.msdk.** { *; }
-keep class com.adcolony.sdk.** { *; }
-keep class com.inmobi.** { *; }
-keep class com.five_corp.** { *; }
-keep class com.bytedance.** { *; }
-keep class com.smaato.** { *; }
-keep class com.explorestack.** { *; }
-keep class io.bidmachine.** { *; }
-keep class com.pubmatic.** { *; }
-keep class com.chartboost.** { *; }
-keep class com.mobilefuse.** { *; }
-keep class com.verve.** { *; }
-keep class net.pubnative.** { *; }
-keep class com.safedk.** { *; }
-keep class com.applovin.quality.** { *; }
-keep class com.braze.** { *; }
-keep class androidx.media3.common.Player {
	void release();
}
