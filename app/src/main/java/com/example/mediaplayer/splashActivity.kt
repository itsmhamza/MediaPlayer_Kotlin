package com.example.mediaplayer

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.mediaplayer.databinding.ActivityPermissionDialogBinding
import com.example.mediaplayer.databinding.ActivitySplashBinding
import com.google.android.gms.ads.MobileAds
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

@SuppressLint("CustomSplashScreen")
class splashActivity : AppCompatActivity() {
    lateinit var binding:ActivitySplashBinding
    private val RequestforPerission = 42
    var perdialog: PersmissionDialog? = null
    companion object{
        var ads:Boolean=false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ads = false
        InterstialAdsCall.getInstance().loadInterstitialAd(this)
        MobileAds.initialize(this)
        NativeAd().ADmodADs(this, this,binding.shimmerlayout,binding.frame)
        Handler().postDelayed({
            binding.progres.visibility = View.GONE
            binding.start.visibility =  View.VISIBLE
            binding.text.visibility = View.VISIBLE
        }, 2000)
        binding.start.setOnClickListener {
            requestforPermissions()
        }
    }
    private fun showSettingsDialog() {
        perdialog = PersmissionDialog(this,this)
        perdialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        perdialog?.setCanceledOnTouchOutside(false)
        perdialog?.show()
    }
    fun requestforPermissions(){
        val intent = Intent(this,MainActivity::class.java)
        Dexter.withContext(this)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    binding.progres.visibility = View.VISIBLE
                    binding.start.visibility = View.GONE
                    binding.text.visibility = View.GONE
                    Handler().postDelayed({
                        startActivity(intent)
                        finish()
                        InterstialAdsCall.getInstance().showInterstitialAdNew(this@splashActivity)
                    },1000)

                    // permission is granted, open the camera
                }
                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    // check for permanent denial of permission
                    if (response.isPermanentlyDenied) {
                        // navigate user to app settings
                    }
                    showSettingsDialog()
                }
                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }
    inner class PersmissionDialog (val mContext: Context, activity: Activity): Dialog(mContext) {
        private lateinit var permissionbinding: ActivityPermissionDialogBinding
        private val  RequestforPerission= 42
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            permissionbinding = ActivityPermissionDialogBinding.inflate(layoutInflater)
            setContentView(permissionbinding.root)

            permissionbinding.btnRead.setOnClickListener {
                perdialog?.dismiss()
                openSettings()
            }
        }
        private fun openSettings() {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", "$packageName", null)
            intent.data = uri
            startActivityForResult(intent,RequestforPerission)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            RequestforPerission -> {
                if (resultCode == RESULT_OK && null != data) {

                } else {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                    }
                }
            }
        }
    }
}