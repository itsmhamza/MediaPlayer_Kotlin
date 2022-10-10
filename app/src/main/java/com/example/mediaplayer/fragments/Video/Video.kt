package com.example.mediaplayer.fragments.Video

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mediaplayer.Data.folderr
import com.example.mediaplayer.Data.getAllVideo
import com.example.mediaplayer.Data.video
import com.example.mediaplayer.databinding.FragmentVideoBinding


public class Video : Fragment() {
private var _binding:FragmentVideoBinding?=null
private val binding get() =_binding
    private var runnable:Runnable? =null
    companion object{
        lateinit var videoList: ArrayList<video>
        lateinit var folderList: ArrayList<folderr>
        lateinit var searchList:ArrayList<video>
        var datachanged:Boolean = false
        var adopterchanged:Boolean = false
        var search:Boolean=false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVideoBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        val adopter = videoAdopter(childFragmentManager)
        adopter.addFragment(videos(),"")
        adopter.addFragment(folder(),"")
        binding?.viewpager?.adapter =adopter
        binding?.tablayout?.setupWithViewPager(binding?.viewpager)
        binding?.tablayout?.getTabAt(0)!!.setText("Videos")
        binding?.tablayout?.getTabAt(1)!!.setText("Folders")
        folderList = ArrayList()
        videoList = getAllVideo(this.requireContext())
        runnable = Runnable {
          if (datachanged){
              try {
                  videoList = getAllVideo(this.requireContext())
                  datachanged = false
                  adopterchanged = true
              }catch (e:Exception){}
          }
            Handler(Looper.getMainLooper()!!).postDelayed(runnable!!,200)
        }
        Handler(Looper.getMainLooper()!!).postDelayed(runnable!!,0)
        if(datachanged) videoList = getAllVideo(this.requireContext())
        return binding?.root
    }



}