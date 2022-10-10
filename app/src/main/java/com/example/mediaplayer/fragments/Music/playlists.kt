package com.example.mediaplayer.fragments.Music

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.AddPlaylistDialogBinding
import com.example.mediaplayer.databinding.FragmentPlaylistsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class playlists : Fragment() {

    private var _binding: FragmentPlaylistsBinding?=null
    private val binding get() =_binding
    private lateinit var adopter: PlaylistAdopter
    companion object{
        var  musicPlaylist:musicPlaylist = musicPlaylist()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentPlaylistsBinding.inflate(inflater,container,false)
        binding?.playlistRecyclerview?.setHasFixedSize(true)
        binding?.playlistRecyclerview?.setItemViewCacheSize(13)
        binding?.playlistRecyclerview?.layoutManager = LinearLayoutManager(this.requireContext())
        adopter = PlaylistAdopter(this.requireContext(),playlistlist = musicPlaylist.ref )
        binding?.playlistRecyclerview?.adapter = adopter
        binding?.addPlaylists?.setOnClickListener {
            customalertialog()
        }
        return binding?.root
    }
    private fun customalertialog(){

        val customDialog = LayoutInflater.from(this.requireContext()).inflate(R.layout.add_playlist_dialog,binding?.root,false)
        val binder = AddPlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this.requireContext())
        builder.setView(customDialog)
            .setTitle("Playlist Details")
            .setPositiveButton("Add"){dialog,_->
                val playlistname = binder.playlistName.text
                val createdBy = binder.userPlaylist.text
                if (playlistname!=null)
                    if (playlistname.isEmpty() || createdBy!!.isEmpty())
                    {
                        Toast.makeText(this.requireContext(), "Please Enter Empty fields", Toast.LENGTH_SHORT).show()
                    }
                else{
                        addPlaylist(playlistname.toString(),createdBy.toString())
                    }
                dialog.dismiss()
            }.create()
        builder.show()
    }

    private fun addPlaylist(name: String, createdBy: String) {
        var playlistExist = false
        for (i in musicPlaylist.ref)
        {
            if (name.equals(i.name))
            {
                playlistExist = true
                break
            }
        }
        if (playlistExist)
        {
            Toast.makeText(this.requireContext(), "Playlist Exist", Toast.LENGTH_SHORT).show()
        }
        else
        {
            val tempplaylist = playlist()
            tempplaylist.name = name
            tempplaylist.playlist = ArrayList()
            tempplaylist.createdBy = createdBy
            val calender = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempplaylist.createdOn= sdf.format(calender)
            musicPlaylist.ref.add(tempplaylist)
            adopter.refreshPlaylist()
        }
    }
    override fun onResume() {
        super.onResume()
        adopter.notifyDataSetChanged()
    }

}