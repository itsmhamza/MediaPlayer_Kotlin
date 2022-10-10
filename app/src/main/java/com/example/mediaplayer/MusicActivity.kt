package com.example.mediaplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.AnimationDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mediaplayer.MainActivity.Companion.favouritechanged
import com.example.mediaplayer.MainActivity.Companion.favtsongs
import com.example.mediaplayer.databinding.ActivityMusicBinding
import com.example.mediaplayer.fragments.Music.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File


class MusicActivity : AppCompatActivity(),ServiceConnection,MediaPlayer.OnCompletionListener {

    companion object
    {
       lateinit var musicListPA:ArrayList<Musics>
        var songPosition:Int = 0
        var isplaying:Boolean=false
        var musicService:MusicService?=null
        lateinit var binding:ActivityMusicBinding
        private var animationDrawable: AnimationDrawable? = null
        var repeat:Boolean=false
        var min15:Boolean= false
        var min30:Boolean= false
        var min60:Boolean= false
        var nowplayingid:String= ""
        var isFavt:Boolean=false
        var toast:Boolean = false
        var findex:Int = -1
    }
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val pref = this.getSharedPreferences("smartbar", Context.MODE_PRIVATE)
        MainActivity.currenttheme = pref?.getString("theme", "0").toString()
        animationDrawable = binding.root.getBackground() as AnimationDrawable
        animationDrawable!!.setEnterFadeDuration(3000)
        animationDrawable!!.setExitFadeDuration(2000)
       // setthemeActivity()
        intialzelayout()
        //immersive mode
        binding.backSong.setOnClickListener {
            if(toast==false) {
                if (musicService != null) {
                    if (MusicActivity.isplaying == false) {
                        MusicActivity.musicService!!.stopForeground(true)
                        MusicActivity.musicService = null
                        MusicActivity.isplaying = false
                    }
                }
                toast=true
            }
            finish()
        }
        binding.playPause.setOnClickListener {
                if (isplaying)
                {pausemusic()}
                else playmusic()
        }
        binding.previousSong.setOnClickListener {
            prenext(increment = false)
        }
        binding.nextSong.setOnClickListener {
            prenext(increment = true)
        }
        binding.seekbarSong.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) musicService!!.mediaPlayer!!.seekTo(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

        })
        binding.repeatSong.setOnClickListener {
            if (!repeat) {
                repeat = true
                Toast.makeText(this, "Repeat On", Toast.LENGTH_SHORT).show()
            } else {
                repeat = false
                Toast.makeText(this, "Repeat Off", Toast.LENGTH_SHORT).show()
            }
        }
        binding.equalizer.setOnClickListener {
            try {
                val eqintent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqintent.putExtra(
                    AudioEffect.EXTRA_AUDIO_SESSION,
                    musicService!!.mediaPlayer!!.audioSessionId
                )
                eqintent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                eqintent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqintent, 13)
            } catch (e: Exception) {
                Toast.makeText(this, "Equlizer Feature not supported", Toast.LENGTH_SHORT).show()
            }
        }
        binding.shareSong.setSafeOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
//            val uri = Uri.fromFile(File(musicListPA[songPosition].path))
            val imageUri = FileProvider.getUriForFile(this,
                "com.example.mediaplayer.provider",  //(use your app signature + ".provider" )
                File(musicListPA[songPosition].path)
            )
            Log.e("asd","$imageUri")
            shareIntent.putExtra(Intent.EXTRA_STREAM,imageUri)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Sharing Music File!!"))

        }
        binding.timerSong.setSafeOnClickListener {
            val timer = min15 || min30 || min60
            if (!timer) showbotsheetdialog()
            else {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("Stop Timer")
                    .setMessage("Do you Want to Stop timer?")
                    .setPositiveButton("Yes") { _, _ ->
                        min15 = false
                        min30 = false
                        min60 = false

                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                val customDialog = builder.create()
                customDialog.show()
            }
        }
        binding.favt.setOnClickListener {
            findex = favrtchecker(musicListPA[songPosition].id)
            if (isFavt) {
                isFavt = false
                binding.favt.setImageResource(R.drawable.ic_favourite_b)
                favtsongs.removeAt(findex)
            }
         else {
            isFavt = true
            binding.favt.setImageResource(R.drawable.ic_baseline_favorite_24)
            favtsongs.add(musicListPA[songPosition])
        }
            favouritechanged = true
        }
    }
    private fun setlayout(){
        findex = favrtchecker(musicListPA[songPosition].id)
        Glide.with(this)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_music_selection).centerCrop())
            .into(binding.songImg)
        binding.songName.text = musicListPA[songPosition].stitle
        if(isFavt) binding.favt.setImageResource(R.drawable.ic_baseline_favorite_24)
        else binding.favt.setImageResource(R.drawable.ic_favourite_b)
    }
    @RequiresApi(Build.VERSION_CODES.S)
    private fun createmediaplayer(){
        try {
            if (musicService!!.mediaPlayer == null  ) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isplaying = true
            binding.playPause.setImageResource(R.drawable.ic_play_b)
            musicService!!.showNotification(R.drawable.ic_play_b)
            binding.startSong.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.endSong.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekbarSong.progress =0
            binding.seekbarSong.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
            nowplayingid = musicListPA[songPosition].id
        }catch (e:Exception){
            return
        }
    }
        @RequiresApi(Build.VERSION_CODES.S)
        private fun intialzelayout() {
            songPosition = intent.getIntExtra("index", 0)
            when (intent.getStringExtra("class")) {
                "playlistdetailsShuffle" -> {
                    val intent = Intent(this, MusicService::class.java)
                    bindService(intent, this, BIND_AUTO_CREATE)
                    startService(intent)
                    musicListPA = ArrayList()
                    musicListPA.addAll(playlists.musicPlaylist.ref[playlist_details.currentplaylistpos].playlist)
                    musicListPA.shuffle()
                    setlayout()
                }
                "playlistDetailsAdopter" -> {
                    val intent = Intent(this, MusicService::class.java)
                    bindService(intent, this, BIND_AUTO_CREATE)
                    startService(intent)
                    musicListPA = ArrayList()
                    musicListPA.addAll(playlists.musicPlaylist.ref[playlist_details.currentplaylistpos].playlist)
                    setlayout()
                }
                "MusicActivity" -> {
                    val intent = Intent(this, MusicService::class.java)
                    bindService(intent, this, BIND_AUTO_CREATE)
                    startService(intent)
                    musicListPA = ArrayList()
                    musicListPA.addAll(allsongs.MusicListMA)
                    musicListPA.shuffle()
                    setlayout()
                }
                "FavouriteShuffle" -> {
                    val intent = Intent(this, MusicService::class.java)
                    bindService(intent, this, BIND_AUTO_CREATE)
                    startService(intent)
                    musicListPA = ArrayList()
                    musicListPA.addAll(favtsongs)
                    musicListPA.shuffle()
                    setlayout()
                }
                "FavrtAdopter" -> {
                    val intent = Intent(this, MusicService::class.java)
                    bindService(intent, this, BIND_AUTO_CREATE)
                    startService(intent)
                    musicListPA = ArrayList()
                    musicListPA.addAll(favtsongs)
                    setlayout()
                }
                "NowPlaying" -> {
                    setlayout()
                    binding.startSong.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                    binding.endSong.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                    binding.seekbarSong.progress = musicService!!.mediaPlayer!!.currentPosition
                    binding.seekbarSong.max = musicService!!.mediaPlayer!!.duration

                    if (isplaying) binding.playPause.setImageResource(R.drawable.ic_play_b)
                    else binding.playPause.setImageResource(R.drawable.ic_pause_b)
                    /*val intent = Intent(this, MusicService::class.java)
                    bindService(intent, this, BIND_AUTO_CREATE)
                    startService(intent)
                    musicListPA = ArrayList()
                    musicListPA.addAll(allsongs.MusicListMA)
                    setlayout()
                    if (isplaying) binding.playPause.setImageResource(R.drawable.ic_play_b)
                    else binding.playPause.setImageResource(R.drawable.ic_pause_b)*/
                }
                "MusicAdopterSearch" -> {
                    val intent = Intent(this, MusicService::class.java)
                    bindService(intent, this, BIND_AUTO_CREATE)
                    startService(intent)
                    musicListPA = ArrayList()
                    musicListPA.addAll(allsongs.musiclistSearch)
                    setlayout()
                }
                "MusicAdopter" -> {
                    val intent = Intent(this, MusicService::class.java)
                    bindService(intent, this, BIND_AUTO_CREATE)
                    startService(intent)
                    musicListPA = ArrayList()
                    musicListPA.addAll(allsongs.MusicListMA)
                    setlayout()

                }
            }
        }
    @RequiresApi(Build.VERSION_CODES.S)
    private fun playmusic(){
        binding.playPause.setImageResource(R.drawable.ic_play_b)
        musicService!!.showNotification(R.drawable.ic_play_b)
        NowPlaying.binding.playpausePlaying.setImageResource(R.drawable.ic_baseline_pause_24)
        isplaying=true
        musicService!!.mediaPlayer!!.start()
    }
    @RequiresApi(Build.VERSION_CODES.S)
    private fun pausemusic(){
        binding.playPause.setImageResource(R.drawable.ic_pause_b)
        musicService!!.showNotification(R.drawable.ic_pause_b)
        NowPlaying.binding.playpausePlaying.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        isplaying =false
        musicService!!.mediaPlayer!!.pause()
    }
    @RequiresApi(Build.VERSION_CODES.S)
    private fun prenext(increment:Boolean){
        if(increment){
            setsongposition(increment = true)
            setlayout()
            createmediaplayer()
        }
        else{
            setsongposition(increment=false)
            setlayout()
            createmediaplayer()
        }
    }
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder=service as MusicService.MyBinder
        musicService=binder.currentServices()
        createmediaplayer()
        musicService!!.seekbarset()
        musicService!!.audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        musicService!!.audioManager.requestAudioFocus(musicService,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService =null
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCompletion(mp: MediaPlayer?) {
        try {
        setsongposition(increment = true)
        createmediaplayer()
        //refresh now playing image
        NowPlaying.binding.songNowplay.isSelected = true
            Glide.with(this)
                .load(musicListPA[songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.ic_music_selection).centerCrop())
                .into(NowPlaying.binding.songImg)
            NowPlaying.binding.songNowplay.text = musicListPA[songPosition].stitle
        }catch (e:java.lang.Exception){}
        try {
            setlayout()}catch (e:java.lang.Exception){
                return
            }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==13  || resultCode== RESULT_OK){
            return

        }
    }

    override fun onResume() {
        super.onResume()
        if(animationDrawable != null && !animationDrawable!!.isRunning)
        {
           animationDrawable!!.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if(animationDrawable != null && animationDrawable!!.isRunning)
        {
            animationDrawable!!.stop()
        }
    }
    private fun showbotsheetdialog(){
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.botsheet_dialog)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener {
            Toast.makeText(baseContext, "Music will stop after 15 minutes", Toast.LENGTH_SHORT).show()
            min15 = true
            Thread{Thread.sleep((15 * 60000).toLong())
                if(min15) exit()
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener {
            Toast.makeText(baseContext, "Music will stop after 30 minutes", Toast.LENGTH_SHORT).show()
            min30 = true
            Thread{Thread.sleep((30 * 60000).toLong())
                if(min30) {
                    exit()
                }
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener {
            Toast.makeText(baseContext, "Music will stop after 60 minutes", Toast.LENGTH_SHORT).show()
            min60 = true
            Thread{Thread.sleep((60 * 60000).toLong())
                if(min60) exit()
            }.start()
            dialog.dismiss()
        }
    }
   /* fun setthemeActivity() {
        var currenttheme: String
        val pref = this.getSharedPreferences("smartbar", Context.MODE_PRIVATE)
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
    }*/
   fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
       val safeClickListener = SafeClickListener {
           onSafeClick(it)
       }
       setOnClickListener(safeClickListener)
   }
    fun exit() {
        if (musicService != null) {
            musicService!!.stopForeground(true)
            musicService!!.mediaPlayer!!.stop()
            isplaying = false
        }
        min15 = false
        min30 = false
        min60 = false
        binding.playPause.setImageResource(R.drawable.ic_pause_b)
        NowPlaying.binding.playpausePlaying.setImageResource(R.drawable.ic_baseline_play_arrow_24)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(toast==false) {
            if (MusicActivity.musicService != null) {
                if (MusicActivity.isplaying == false) {
                    MusicActivity.musicService!!.stopForeground(true)
                    MusicActivity.musicService = null
                    MusicActivity.isplaying = false
                }
            }
            toast=true
        }
        finish()
    }

}