package com.example.mediaplayer.Data

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.mediaplayer.R
import com.example.mediaplayer.fragments.Video.Video
import java.io.File

data class video(val id:String, var title:String, val duration: Long=0,
                 val folderName:String, val size:String, var path:String, var artUri:Uri,
)

data class folderr(val id :String,val folderName: String)

data class Themes(var titleImage: Int, var heading : String)
@SuppressLint("Range")
 fun getAllVideo(context: Context):ArrayList<video>{
    val tempList = ArrayList<video>()
    val tempfolder = ArrayList<String>()
    val projection = arrayOf(
        MediaStore.Video.Media.TITLE, MediaStore.Video.Media.SIZE,
        MediaStore.Video.Media._ID, MediaStore.Video.Media.BUCKET_DISPLAY_NAME
        , MediaStore.Video.Media.DATA, MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media.DURATION,
        MediaStore.Video.Media.BUCKET_ID)
    val cursor = context?.contentResolver?.query(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,projection,
        null,null, MediaStore.Video.Media.DATE_ADDED + " DESC")
    if (cursor!=null)
        if (cursor.moveToNext())
            do {
                val titleC =  cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))?:"Unknown"
                val idC =  cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))?:"Unknown"
                val folderC =  cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))?:"Internal Storage"
                val folderIdC =  cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_ID))?:"Unknown"
                val sizeC =  cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))?:"0"
                val pathC =  cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))?:"Unknown"
                val durationC =  cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))?.toLong()?:0L

                try {
                    val file = File(pathC)
                    val artUriC = Uri.fromFile(file)
                    val video = video(title = titleC,id = idC,folderName = folderC,
                        duration = durationC,size = sizeC,path = pathC,artUri = artUriC)
                    if (file.exists()) tempList.add(video)
                    //for addding folder
                    if (!tempfolder.contains(folderC)){
                        tempfolder.add(folderC)
                        Video.folderList.add(folderr(id = folderIdC,folderName = folderC))
                    }
                }catch (e:Exception){}
            } while (cursor.moveToNext())
    cursor?.close()
    return tempList
}


object List_themes{
    var themes = arrayListOf(
        Themes(R.color.bg_color,"0"),
        Themes(R.drawable.bg1,"1"),
        Themes(R.drawable.bg2,"2"),
        Themes(R.drawable.bg3,"3"),
        Themes(R.drawable.bg4,"4"),
        Themes(R.drawable.bg5,"5"),
        Themes(R.drawable.bg6,"6"),
        Themes(R.drawable.bg7,"7")

    )
}

