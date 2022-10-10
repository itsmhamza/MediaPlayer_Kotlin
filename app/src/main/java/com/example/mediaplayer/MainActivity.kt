package com.example.mediaplayer

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.mediaplayer.Home.Home
import com.example.mediaplayer.R.*
import com.example.mediaplayer.Themes.Themes
import com.example.mediaplayer.databinding.ActivityMainBinding
import com.example.mediaplayer.fragments.Music.*
import com.example.mediaplayer.fragments.Setting
import com.example.mediaplayer.fragments.Video.Video
import com.example.mediaplayer.fragments.Video.VideoRAdopter
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.common.reflect.TypeToken
import com.google.gson.GsonBuilder


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var adopter:VideoRAdopter
    private var count:Int=0
    companion object
    {
        var currenttheme = String()
        var favtsongs:ArrayList<Musics> = ArrayList()
        var favouritechanged:Boolean=false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(style.Theme_MediaPlayer)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //retrieve
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
        val jsonString = editor.getString("favour",null)
        val typetoken = object : TypeToken<ArrayList<Musics>>(){}.type
        if(jsonString!=null){
            favtsongs.clear()
            val data:ArrayList<Musics> = GsonBuilder().create().fromJson(jsonString,typetoken)
            favtsongs.addAll(data)
        }
        playlists.musicPlaylist =  musicPlaylist()
        val jsonstrringplaylist = editor.getString("musicplaylist",null)
        if(jsonstrringplaylist!=null){
            val dataplaylist:musicPlaylist = GsonBuilder().create().fromJson(jsonstrringplaylist,musicPlaylist::class.java)
            playlists.musicPlaylist =dataplaylist
        }
        splashActivity.ads = true
        NativeAd().ADmodADsSmall(this ,this,binding.shimmerlayout,binding.frame)
        val pref = this.getSharedPreferences("smartbar", Context.MODE_PRIVATE)
        binding.bottomNavigationView.setSelectedItemId(id.home)
        setSupportActionBar(binding.toolbar)
        currenttheme = pref?.getString("theme", "0").toString()
        setthemeActivity()
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                id.home -> {
                    replaceFragment(Home())
                    count++
                }
                id.video -> {
                    replaceFragment(Video())
                    count++
                }
                id.music -> {
                    replaceFragment(Music())
                    count++
                }
                id.themes -> {
                    replaceFragment(Themes())
                    count++
                }
                id.setting -> {
                    replaceFragment(Setting())
                    count++
                }
            }
            if(count == 3) {
                if (MusicActivity.musicService!=null){
                    if(MusicActivity.isplaying==false){
                        MusicActivity.musicService!!.stopForeground(true)
                        MusicActivity.musicService = null
                        MusicActivity.isplaying=false
                    }
                }
                InterstialAdsCall.getInstance().showInterstitialAdNew(this)
                count = 0
            }
            NativeAd().ADmodADsSmall(this, this,binding.shimmerlayout,binding.frame)
            return@setOnItemSelectedListener true
        }
    }
    private fun replaceFragment(fragment: Fragment) {

        val transcation = supportFragmentManager.beginTransaction()
            transcation.replace(id.fragmentContainerView,fragment)
            transcation.disallowAddToBackStack()
            transcation.commit()
    }

    fun setthemeActivity() {
        val currenttheme: String
        val pref = this.getSharedPreferences("smartbar",Context.MODE_PRIVATE)
        currenttheme =pref?.getString("theme","0").toString()
        if (currenttheme.toInt() ==0){
            binding.root.setBackgroundResource(R.color.bg_color)
        }
        else if (currenttheme.toInt() ==1){
            binding.root.setBackgroundResource(R.drawable.bg1)
        }
        else if (currenttheme.toInt() ==2){
            binding.root.setBackgroundResource(R.drawable.bg2)
        }
        else if (currenttheme.toInt() ==3){
            binding.root.setBackgroundResource(R.drawable.bg3)
        }
        else if (currenttheme.toInt() ==4){
            binding.root.setBackgroundResource(R.drawable.bg4)
        }
        else if (currenttheme.toInt() ==5){
            binding.root.setBackgroundResource(R.drawable.bg5)
        }
        else if (currenttheme.toInt() ==6){
            binding.root.setBackgroundResource(R.drawable.bg6)
        }
        else if (currenttheme.toInt() ==7){
            binding.root.setBackgroundResource(R.drawable.bg7)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.toolbar_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            id.rate ->  {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + this.packageName)))
                }
                catch (e: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + this.packageName)))
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if(!MusicActivity.isplaying && MusicActivity.musicService!=null){
            exitApplication()
        }
    }
    override fun onResume() {
        super.onResume()
        //for store
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString= GsonBuilder().create().toJson(favtsongs)
        editor.putString("favour",jsonString)
        val jsonStringplaylist= GsonBuilder().create().toJson(playlists.musicPlaylist)
        editor.putString("musicplaylist",jsonStringplaylist)
        editor.apply()
    }
    override fun onBackPressed() {
        val back_pressdialog = Dialog(this)
        back_pressdialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        back_pressdialog.setContentView(R.layout.onbackpress_dialog)
        back_pressdialog.setCanceledOnTouchOutside(false)
        back_pressdialog.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(back_pressdialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        back_pressdialog.window!!.attributes = lp
        val exitApp = back_pressdialog.findViewById<TextView>(R.id.exitbtn)
        val notNow = back_pressdialog.findViewById<TextView>(R.id.notnowbtn)
        val rateIt = back_pressdialog.findViewById<TextView>(R.id.ratebtn)
        val shimmer = back_pressdialog.findViewById<ShimmerFrameLayout>(R.id.shimmerlayouts)
        val frame = back_pressdialog.findViewById<FrameLayout>(R.id.framesss)
        back_pressdialog.show()
        NativeAd().ADmodADs(this,this,shimmer,frame)
        notNow.setOnClickListener {
            back_pressdialog.dismiss()
        }
        exitApp.setOnClickListener {
            back_pressdialog.dismiss()
            finish()
        }
        rateIt.setOnClickListener {
            back_pressdialog.dismiss()
            val uri = Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }
}