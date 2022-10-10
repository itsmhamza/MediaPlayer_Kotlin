package com.example.mediaplayer.fragments.Music

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mediaplayer.databinding.FragmentMusicBinding


class Music : Fragment() {
    private var _binding: FragmentMusicBinding?=null
    private val binding get() =_binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMusicBinding.inflate(inflater,container,false)
        val adopter = audioAdopter(childFragmentManager)
        adopter.addFragment(allsongs(),"")
        adopter.addFragment(playlists(),"")
        adopter.addFragment(favourite(),"")
        binding?.mviewpager?.adapter = adopter
        binding?.mtablayout?.setupWithViewPager(binding?.mviewpager)
        binding?.mtablayout?.getTabAt(0)!!.setText("All Songs")
        binding?.mtablayout?.getTabAt(1)!!.setText("Playlist")
        binding?.mtablayout?.getTabAt(2)!!.setText("Favourites")
        return binding?.root
    }


}