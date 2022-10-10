package com.example.mediaplayer.fragments.Music

import android.media.MediaMetadataRetriever
import com.example.mediaplayer.MainActivity.Companion.favtsongs
import com.example.mediaplayer.MusicActivity
import com.example.mediaplayer.R
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class Musics(val id:String,val stitle:String,val album:String,
                  val artist:String,val duration:Long =0,val path:String,val artUri: String) {
}

class playlist{
    lateinit var name:String
    lateinit var playlist:ArrayList<Musics>
    lateinit var createdBy:String
    lateinit var createdOn:String
}
class musicPlaylist{

    var ref:ArrayList<playlist> = ArrayList()
}
fun formatDuration(duration: Long):String{
    val minutes = TimeUnit.MINUTES.convert(duration,TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(duration,TimeUnit.MILLISECONDS)
    -minutes*TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES))
    return String.format("%02d:%02d",minutes,seconds)

}
fun getimgart(path: String): ByteArray? {
    val retriever  = MediaMetadataRetriever()
    try {
        retriever.setDataSource(path)
    }
   catch (e:Exception){ }
    return retriever.embeddedPicture
}
 fun setsongposition(increment: Boolean){
  if (!MusicActivity.repeat){
      if (increment){
          if(MusicActivity.musicListPA.size-1== MusicActivity.songPosition)
              MusicActivity.songPosition =0
          else ++MusicActivity.songPosition
      }else{
          if(0== MusicActivity.songPosition)
              MusicActivity.songPosition = MusicActivity.musicListPA.size-1
          else --MusicActivity.songPosition
      }
  }
}
fun exitApplication(){
    if(MusicActivity.musicService!=null){
        MusicActivity.musicService!!.audioManager.abandonAudioFocus(MusicActivity.musicService)
        MusicActivity.musicService!!.stopForeground(true)
        MusicActivity.musicService!!.mediaPlayer!!.release()
        MusicActivity.musicService =null
    }
    exitProcess(1)
}
fun exit(){
    if(MusicActivity.musicService !=null){
        MusicActivity.musicService!!.stopForeground(true)
        MusicActivity.musicService!!.mediaPlayer!!.stop()
        MusicActivity.isplaying = false
    }
    MusicActivity.min15 =false
    MusicActivity.min30 =false
    MusicActivity.min60 =false
    MusicActivity.binding.playPause.setImageResource(R.drawable.ic_pause_b)
    NowPlaying.binding.playpausePlaying.setImageResource(R.drawable.ic_baseline_play_arrow_24)
}
fun favrtchecker(id:String):Int{
    MusicActivity.isFavt = false
    favtsongs.forEachIndexed{
        index, musics ->
        if(id == musics.id){
            MusicActivity.isFavt = true
            return index
        }
    }
    return -1
}
fun checkdata(playlist:ArrayList<Musics>):ArrayList<Musics>{
    try {
        playlist.forEachIndexed { index, musics ->
            val file = File(musics.path)
            if(!file.exists())
                playlist.removeAt(index)
        }
    }catch (e:Exception){}
    return playlist
}