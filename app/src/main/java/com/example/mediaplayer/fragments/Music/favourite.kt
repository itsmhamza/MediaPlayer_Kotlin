package com.example.mediaplayer.fragments.Music

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaplayer.MainActivity.Companion.favouritechanged
import com.example.mediaplayer.MainActivity.Companion.favtsongs
import com.example.mediaplayer.MusicActivity
import com.example.mediaplayer.databinding.FragmentFavouriteBinding


class favourite : Fragment() {
    private var _binding: FragmentFavouriteBinding?=null
    lateinit var adopter:favouriteAdopter
    private val binding get() =_binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentFavouriteBinding.inflate(inflater, container, false)
        favtsongs= checkdata(favtsongs)
        binding?.favtRecyclerview?.setHasFixedSize(true)
        binding?.favtRecyclerview?.setItemViewCacheSize(13)
        binding?.favtRecyclerview?.layoutManager = GridLayoutManager(this.requireContext(),4)
        adopter = favouriteAdopter(this.requireContext(), favtsongs)
        binding?.favtRecyclerview?.adapter = adopter
        favouritechanged = false
        if (favtsongs.size<1) binding?.favtShuffle?.visibility = View.INVISIBLE
        binding?.favtShuffle?.setOnClickListener {
            val intent = Intent(requireContext(), MusicActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "FavouriteShuffle")
            startActivity(intent)
        }
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        if (favouritechanged){
            adopter.updatefavourite(favtsongs)
            favouritechanged=false
        }
        if (favtsongs.size<1) binding?.favtShuffle?.visibility = View.INVISIBLE
    }

}