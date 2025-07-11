package com.example.mediaplayer

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.mediaplayer.Data.DoubleClickListener
import com.example.mediaplayer.Data.video
import com.example.mediaplayer.databinding.ActivityPlayerBinding
import com.example.mediaplayer.databinding.MoreFeaturesBinding
import com.example.mediaplayer.databinding.SpeedDialogBinding
import com.example.mediaplayer.fragments.Video.Video
import com.example.mediaplayer.fragments.Video.folderActivity
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.TimeBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class PlayerActivity : AppCompatActivity(),GestureDetector.OnGestureListener,
    AudioManager.OnAudioFocusChangeListener {
    lateinit var binding:ActivityPlayerBinding
    lateinit var runnable: Runnable
    private lateinit var gestureDetectorCompat: GestureDetectorCompat
    companion object {
        private var audioManager:AudioManager?=null
        lateinit var player: ExoPlayer
        lateinit var playerlist :ArrayList<video>
        var position:Int = -1
        private var repeat:Boolean = false
        private var fullscreen:Boolean = false
        lateinit var trackselector:DefaultTrackSelector
        private var speed:Float = 1.0f
        var pipstatus:Int = 0
        private var brightness:Int =0
        private var volume:Int =0
        var nowPlayingid:String = ""
        var toast:Boolean = false
        //settings
        var orientationn:Boolean = false
        var popup:Boolean=false
        var gesture:Boolean=false
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        gestureDetectorCompat = GestureDetectorCompat(this,this)
        //immersive mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        //for handling video intent
       try {
           if (intent.data?.scheme.contentEquals("content")){
               playerlist = ArrayList()
               position = 0
               val cursor = contentResolver.query(intent.data!!, arrayOf(MediaStore.Video.Media.DATA),
                   null,null,null)
               cursor?.let {
                   it.moveToFirst()
                   val path =  it.getString(it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                   val file = File(path)
                   val video = video(id = "",title = file.name,duration = 0L,artUri = Uri.fromFile(file)
                       ,path = path,size="",folderName = "")
                   playerlist.add(video)
                   cursor.close()
               }
               createplayer()
               intialzingBinding()
           }else{
               intializelayout()
               intialzingBinding()
           }
       }catch (e:Exception){
           Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
       finish()
       }

    }
    private fun intializelayout(){
        when(intent.getStringExtra("class")){

            "AllVideos"->{
                playerlist = ArrayList()
                playerlist.addAll(Video.videoList)
                createplayer()
            }
            "folderActivity"->{

                playerlist = ArrayList()
                playerlist.addAll(folderActivity.currentfolderVideos)
                createplayer()
            }
            "SearchedVideo"->{

                playerlist = ArrayList()
                playerlist.addAll(Video.searchList)
                createplayer()
            }
            "NowPlaying"->{
                if(MusicActivity.musicService !=null){
                    MusicActivity.musicService!!.stopForeground(true)
                    MusicActivity.musicService!!.mediaPlayer!!.pause()
                    MusicActivity.isplaying = false
                }
                speed = 1.0f
                binding.videoTitle.text = playerlist[position].title
                binding.videoTitle.isSelected = true
                binding.playerView.player = player
                playvideo()
                playInFullscreen(enable = fullscreen)
                setVisibilty()
                seekbarFeature()
            }
        }
    }
    @SuppressLint("SetTextI18n", "ClickableViewAccessibility", "ResourceType",
        "SuspiciousIndentation"
    )
    private fun intialzingBinding(){
        val status =  this.getSharedPreferences("tap",Context.MODE_PRIVATE)
            ?.getBoolean("status2",true)
        if(status!!) {
            binding.forwardFL.setOnClickListener(DoubleClickListener(callback = object :
                DoubleClickListener.Callback {
                override fun doubleClicked() {
                    binding.playerView.showController()
                    binding.forward.visibility = View.VISIBLE
                    player.seekTo(player.currentPosition + 10000)
                }
            }))
            binding.rewindFL.setOnClickListener(DoubleClickListener(callback = object :
                DoubleClickListener.Callback {
                override fun doubleClicked() {
                    binding.playerView.showController()
                    binding.rewind.visibility = View.VISIBLE
                    player.seekTo(player.currentPosition - 10000)
                }
            }))
        }
        binding.playerView.setOnTouchListener { _, motionevent ->
            gestureDetectorCompat.onTouchEvent(motionevent)
            if (motionevent.action== MotionEvent.ACTION_UP)
            {
                binding.brigthness.visibility = View.GONE
                binding.volume.visibility = View.GONE
                //immersive mode
                WindowCompat.setDecorFitsSystemWindows(window, false)
                WindowInsetsControllerCompat(window, binding.root).let { controller ->
                    controller.hide(WindowInsetsCompat.Type.systemBars())
                    controller.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
            return@setOnTouchListener false
        }
        binding.orientation.setOnClickListener {
            if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            else
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        binding.backButton.setOnClickListener {
            if (popup) {
                popup = true
                val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                val status = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    appOps.checkOpNoThrow(
                        AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
                        android.os.Process.myUid(),
                        packageName
                    ) == AppOpsManager.MODE_ALLOWED
                } else {
                    false
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (status) {
                        this.enterPictureInPictureMode(PictureInPictureParams.Builder().build())
                        binding.playerView.hideController()
                        playvideo()
                        pipstatus = 0
                    } else { try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val intent = Intent(
                                "android.settings.PICTURE_IN_PICTURE_SETTINGS",
                                Uri.parse("Package:$packageName")
                            )
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Feature not supported", Toast.LENGTH_SHORT).show()
                            playvideo()
                        }
                    }
                    catch (e:Exception){
                        if(toast==false) {
                            Toast.makeText(
                                this,
                                "Enable Picture in Picture mode",
                                Toast.LENGTH_SHORT
                            ).show()
                            toast = true
                        }
                        finish()
                    }
                    }
                }
            }else{
                finish()
            }
        }
        binding.playPause.setOnClickListener {
            if (player.isPlaying) pausevideo()
            else playvideo()
        }
        binding.repeat.setOnClickListener {
            if(repeat){
                repeat = false
                player.repeatMode = Player.REPEAT_MODE_OFF
                Toast.makeText(this, "Repeat Mode OFF", Toast.LENGTH_SHORT).show()
            }else{
                repeat = true
                player.repeatMode = Player.REPEAT_MODE_ONE
                Toast.makeText(this, "Repeat Mode ON", Toast.LENGTH_SHORT).show()
            }
        }
        binding.fullsrceen.setOnClickListener {
            if (fullscreen){
                    fullscreen = false
                playInFullscreen(enable = false)
            }else{
                fullscreen = true
                playInFullscreen(enable = true)
            }
        }
        binding.next.setOnClickListener {
            nextprevideo()
        }
        binding.previous.setOnClickListener {
            nextprevideo(isNext = false)
        }
        binding.moreFeatures.setOnClickListener {
            pausevideo()
            val customDialog = LayoutInflater.from(this).inflate(R.layout.more_features,binding.root,false)
            val bindingMF = MoreFeaturesBinding.bind(customDialog)
            val dialog = MaterialAlertDialogBuilder(this).setView(customDialog)
                .setOnCancelListener{
                    playvideo() }
                .setBackground(ColorDrawable(0x80AAB0FB.toInt()))
                .create()
            dialog.show()
            bindingMF.audioTrack.setOnClickListener {
                dialog.dismiss()
                playvideo()
                val audioTrack = ArrayList<String>()
                val audiolist = ArrayList<String>()
                for(group in player.currentTracksInfo.trackGroupInfos){
                    if(group.trackType == C.TRACK_TYPE_AUDIO){
                        val groupInfo = group.trackGroup
                        for (i in 0 until groupInfo.length){
                            audioTrack.add(groupInfo.getFormat(i).language.toString())
                            audiolist.add("${audiolist.size + 1}. " + Locale(groupInfo.getFormat(i).language.toString()).displayLanguage
                                    + " (${groupInfo.getFormat(i).label})")
                        }
                    }
                }
             //  if (audiolist[0].contains("null")) audiolist[0] = "1. Default Track"
                val temptracks = audiolist.toArray(arrayOfNulls<CharSequence>(audiolist.size))
                val audiodialog= MaterialAlertDialogBuilder(this, R.style.alertDialog)
                    .setTitle("Select Language")
                    .setOnCancelListener {
                        playvideo()
                    }
                    .setPositiveButton("Off Audio"){ self, _ ->
                        trackselector.setParameters(trackselector.buildUponParameters().setRendererDisabled(
                            C.TRACK_TYPE_AUDIO, true
                        ))
                        self.dismiss()
                    }
                    .setItems(temptracks){ _,position ->
                        Snackbar.make(binding.root,audiolist[position]+" Selected",3000).show()
                        trackselector.setParameters(trackselector.buildUponParameters()
                            .setRendererDisabled(C.TRACK_TYPE_AUDIO,false)
                            .setPreferredAudioLanguage(audioTrack[position]))
                    }
                    .create()
                    audiodialog.show()
                audiodialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE)
                audiodialog.window?.setBackgroundDrawable(ColorDrawable(0x99000000.toInt()))
            }
            bindingMF.speed.setOnClickListener {
                dialog.dismiss()
                playvideo()
                val customDialogS = LayoutInflater.from(this).inflate(R.layout.speed_dialog,binding.root,false)
                val bindingS = SpeedDialogBinding.bind(customDialogS)
                val dialogS = MaterialAlertDialogBuilder(this).setView(customDialogS)
                    .setCancelable(false)
                    .setPositiveButton("OK"){self,_->
                        self.dismiss()
                    }
                    .setBackground(ColorDrawable(0x80FFFFFF.toInt()))
                    .create()
                dialogS.show()
                bindingS.speedtext.text = "${DecimalFormat("#.##").format(speed)} X"
                bindingS.minus.setOnClickListener {
                    changespeed(isIncrement = false)
                    bindingS.speedtext.text = "${DecimalFormat("#.##").format(speed)} X"
                }
                bindingS.plus.setOnClickListener {
                    changespeed(isIncrement = true)
                    bindingS.speedtext.text = "${DecimalFormat("#.##").format(speed)} X"
                }
            }
            bindingMF.pip.setOnClickListener {
                    val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                    val status = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        appOps.checkOpNoThrow(
                            AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
                            android.os.Process.myUid(),
                            packageName
                        ) == AppOpsManager.MODE_ALLOWED
                    } else {
                        false
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (status) {
                            this.enterPictureInPictureMode(PictureInPictureParams.Builder().build())
                            dialog.dismiss()
                            binding.playerView.hideController()
                            playvideo()
                            pipstatus = 0
                        } else { try {
                            val intent = Intent(
                                "android.settings.PICTURE_IN_PICTURE_SETTINGS",
                                Uri.parse("Package:$packageName")
                            )
                            startActivity(intent)
                        }catch (e:Exception){
                            Toast.makeText(this, "Enable Picture in Picture mode", Toast.LENGTH_SHORT).show()
                        }
                        }
                    } else {
                        Toast.makeText(this, "Feature not supported", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        playvideo()
                    }
                }
            bindingMF.subtitles.setOnClickListener {
                dialog.dismiss()
                playvideo()
                val subtitles = ArrayList<String>()
                val subtitlelist = ArrayList<String>()
                for(group in player.currentTracksInfo.trackGroupInfos){
                    if(group.trackType == C.TRACK_TYPE_TEXT){
                        val groupInfo = group.trackGroup
                        for (i in 0 until groupInfo.length){
                            subtitles.add(groupInfo.getFormat(i).language.toString())
                            subtitlelist.add("${subtitlelist.size + 1}. " + Locale(groupInfo.getFormat(i).language.toString()).displayLanguage
                                    + " (${groupInfo.getFormat(i).label})")
                        }
                    }
                }
                val temptracks = subtitlelist.toArray(arrayOfNulls<CharSequence>(subtitlelist.size))
                val sdialog= MaterialAlertDialogBuilder(this, R.style.alertDialog)
                    .setTitle("Select Subtitles")
                    .setOnCancelListener {
                        playvideo()
                    }
                    .setPositiveButton("Off Subtitiles"){ self, _ ->
                        trackselector.setParameters(trackselector.buildUponParameters().setRendererDisabled(
                            C.TRACK_TYPE_VIDEO, true
                        ))
                        self.dismiss()
                    }
                    .setItems(temptracks){ _,position ->
                        Snackbar.make(binding.root,subtitlelist[position]+" Selected",3000).show()
                        trackselector.setParameters(trackselector.buildUponParameters()
                            .setRendererDisabled(C.TRACK_TYPE_VIDEO,false)
                            .setPreferredTextLanguage(subtitles[position]))
                    }
                    .create()
                sdialog.show()
                sdialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE)
               sdialog.window?.setBackgroundDrawable(ColorDrawable(0x99000000.toInt()))
            }
        }
    }
    private fun  createplayer(){
        try {
            if(MusicActivity.musicService !=null){
                MusicActivity.musicService!!.stopForeground(true)
                MusicActivity.musicService!!.mediaPlayer!!.pause()
                MusicActivity.isplaying = false
            }
            try {
                player.release()
            }catch (e:Exception){}
        }catch (e:Exception){}
        speed = 1.0f
        trackselector = DefaultTrackSelector(this)
        binding.videoTitle.text = playerlist[position].title
        binding.videoTitle.isSelected = true
        player = ExoPlayer.Builder(this).setTrackSelector(trackselector).build()
        binding.playerView.player = player
        val mediaItem = MediaItem.fromUri(playerlist[position].artUri)
        player.setMediaItem(mediaItem)
        player.prepare()
        playvideo()
        binding.playPause.setImageResource(R.drawable.ic_play_b)
        if (orientationn) {
            orientationn=true
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }else
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        player.addListener(object :Player.Listener{
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if(playbackState == Player.STATE_ENDED) nextprevideo()
            }
        })
        playInFullscreen(enable = fullscreen)
        setVisibilty()
        nowPlayingid = playerlist[position].id
            seekbarFeature()
    }

    private fun playvideo(){
        player.seekTo(player.currentPosition + 10)
        binding.playPause.setImageResource(R.drawable.ic_play_b)
        player.play()
    }
    private fun pausevideo(){
        binding.playPause.setImageResource(R.drawable.ic_pause_b)
        player.pause()
    }
    private fun nextprevideo(isNext:Boolean=true){
        if (isNext)  setposition()
        else setposition(isIncrement = false)
        createplayer()
    }
    private fun setposition(isIncrement:Boolean=true) {
        if (!repeat) {
            if (isIncrement) {
                if (playerlist.size - 1 == position)
                    position = 0
                else ++position
            } else {
                if (position == 0)
                    position = playerlist.size - 1
                else --position
            }
        }
    }
    private fun playInFullscreen(enable:Boolean){
        if(enable){
            binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            binding.fullsrceen.setImageResource(R.drawable.ic_default_sc)
        }else{
            binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
            binding.fullsrceen.setImageResource(R.drawable.ic_aspect_b)
        }
    }
    fun setVisibilty(){
        runnable = Runnable {
            if(binding.playerView.isControllerVisible) changevisibilty(View.VISIBLE)
            else changevisibilty(View.INVISIBLE)
            Handler(Looper.myLooper()!!).postDelayed(runnable,200)
        }
        Handler(Looper.myLooper()!!).postDelayed(runnable,0)
    }
    private fun changevisibilty(visibility: Int){
        binding.topController.visibility = visibility
        binding.botController.visibility = visibility
        binding.playPause.visibility = visibility
        binding.rewind.visibility = View.GONE
        binding.forward.visibility = View.GONE
    }
    private fun changespeed(isIncrement: Boolean){
        if(isIncrement){
            if(speed <=2.9f){
                speed +=0.20f
            }
        }else{
            if (speed >0.20f){
                speed -=0.10f
            }
        }
        player.setPlaybackSpeed(speed)
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

        if (pipstatus != 0) {
            finish()
            val intent = Intent(this, PlayerActivity::class.java)
            when (pipstatus) {
                1 -> intent.putExtra("class", "folderActivity")
                2 -> intent.putExtra("class", "SearchedVideo")
                3 -> intent.putExtra("class", "AllVideos")
            }
            startActivity(intent)
        }

        if (!isInPictureInPictureMode) pausevideo()
    }



    private fun seekbarFeature() {
        val timeBar = binding.playerView.findViewById<DefaultTimeBar>(R.id.exo_progress)

        timeBar.addListener(object : TimeBar.OnScrubListener {
            override fun onScrubStart(timeBar: TimeBar, position: Long) {
                pausevideo()
            }

            override fun onScrubMove(timeBar: TimeBar, position: Long) {
                player.seekTo(position)
            }

            override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                playvideo()
            }
        })
    }


    private fun screenbrightness(value:Int){

        val d = 1.0f/30
        val lp = this.window.attributes
        lp.screenBrightness = d * value
        this.window.attributes = lp
    }
    override fun onPause() {
        super.onPause()
        if(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                !isInPictureInPictureMode
            } else {
                TODO("VERSION.SDK_INT < N")
            }
        ) {
            player.pause()
            binding.playPause.setImageResource(R.drawable.ic_pause_b)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            player.pause()
            audioManager?.abandonAudioFocus(this)
        }catch (e:Exception){}
    }
    override fun onResume() {
        if (brightness!=0)
            screenbrightness(brightness)
        super.onResume()
//        if (audioManager == null)
//            audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        audioManager!!.requestAudioFocus(this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN)
    }
    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange<=0) pausevideo()
    }

    override fun onDown(p0: MotionEvent): Boolean = false

    override fun onFling(p0: MotionEvent?, p1: MotionEvent, p2: Float, p3: Float) : Boolean = false

    override fun onLongPress(p0: MotionEvent) = Unit

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent, p2: Float, p3: Float): Boolean {
        val screenwidth = Resources.getSystem().displayMetrics.widthPixels
//        val screenheight = Resources.getSystem().displayMetrics.heightPixels
//        val border = 100 * Resources.getSystem().displayMetrics.density.toInt()
//        if (event!!.x < border || event!!.y < border || event.x > screenwidth -border || event.y > screenheight-border)
//            return false
        if (gesture) {
            gesture = true
            if (abs(p2) < abs(p3)) {
                if (p0!!.x < screenwidth / 2) {
                    binding.brigthness.visibility = View.VISIBLE
                    binding.volume.visibility = View.GONE
                    val increse = p3 > 0
                    val value = if (increse) brightness + 1 else brightness - 1
                    if (value in 0..30) brightness = value
                    binding.brigthness.text = brightness.toString()
                    screenbrightness(brightness)
                } else {
                    binding.brigthness.visibility = View.GONE
                    binding.volume.visibility = View.VISIBLE
                    val maxvolume = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                    val increse = p3 > 0
                    val value = if (increse) volume + 1 else volume - 1
                    if (value in 0..maxvolume) volume = value
                    binding.volume.text = volume.toString()
                    audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
                }
            }
        }
        return true
    }

    override fun onShowPress(p0: MotionEvent) = Unit

    override fun onSingleTapUp(p0: MotionEvent): Boolean = false

}