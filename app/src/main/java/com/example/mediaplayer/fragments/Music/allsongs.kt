package com.example.mediaplayer.fragments.Music

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentAllsongsBinding
import java.io.File

class allsongs : Fragment() {
    private var _binding: FragmentAllsongsBinding?=null
    private val binding get() =_binding
    private lateinit var musicAdopter: MusicAdopter

    companion object{
       lateinit var MusicListMA : ArrayList<Musics>
       lateinit var musiclistSearch:ArrayList<Musics>
       var search:Boolean=false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentAllsongsBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding?.toolbar)
        search  = false
        MusicListMA = getAllmusic()
        binding?.musicrecyclerView?.setHasFixedSize(true)
        binding?.musicrecyclerView?.setItemViewCacheSize(13)
        binding?.musicrecyclerView?.layoutManager = LinearLayoutManager(this.requireContext())
        musicAdopter = MusicAdopter(this.requireContext(), MusicListMA)
        binding?.musicrecyclerView?.adapter = musicAdopter
//        binding?.totalSongs?.text = "Total Songs : "+musicAdopter.itemCount
        return binding?.root
    }
    @SuppressLint("Range")
    private fun getAllmusic():ArrayList<Musics>{
        val templist = ArrayList<Musics>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " !=0"
        val projection = arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATE_ADDED,MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ALBUM_ID)
        val cursor = context?.contentResolver?.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            ,projection,selection,null,MediaStore.Audio.Media.DATE_ADDED+" DESC ",null)
        if (cursor!=null) {
            if (cursor.moveToFirst())
                do {
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))?:"Unknown"
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))?:"Unknown"
                    val albumC =cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))?:"Unknown"
                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))?:"Unknown"
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumidC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri,albumidC).toString()
                    val music = Musics(
                        id = idC,
                        stitle = titleC,
                        album = albumC,
                        artist = artistC,
                        path = pathC,
                        duration = durationC,
                        artUri = artUriC
                    )
                    val file = File(music.path)
                    if (file.exists())
                        templist.add(music)
                } while (cursor.moveToNext())
            cursor?.close()
        }
        return templist
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu_song,menu)
        val searchView = menu.findItem(R.id.search_view)?.actionView as androidx.appcompat.widget.SearchView
        searchView.setBackgroundResource(R.drawable.custom_search_view)
        searchView.setQueryHint("Search Songs")
        searchView.setOnQueryTextListener(object :androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean=true

            override fun onQueryTextChange(newText: String?): Boolean {
                musiclistSearch= ArrayList()
                if(newText!=null){
                    val userInput =  newText.lowercase()
                    for (songs in MusicListMA)
                        if(songs.stitle.lowercase().contains(userInput))
                            musiclistSearch.add(songs)
                    search = true
                    musicAdopter.updatemusicList(searchList = musiclistSearch)
                }
                return true
            }

        })
        super.onCreateOptionsMenu(menu, inflater)
    }

}