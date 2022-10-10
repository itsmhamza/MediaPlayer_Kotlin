package com.example.mediaplayer.Themes

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.Data.List_themes
import com.example.mediaplayer.Data.Themes
import com.example.mediaplayer.InterstialAdsCall
import com.example.mediaplayer.MainActivity
import com.example.mediaplayer.MusicActivity
import com.example.mediaplayer.databinding.FragmentThemesBinding


class Themes : Fragment() {
    private var _binding: FragmentThemesBinding?=null
    private val binding get() =_binding
    private var _recyclerview:RecyclerView?=null
    private lateinit var tempArrayList:ArrayList<Themes>
    private lateinit var newArrayList:ArrayList<Themes>
    var toast:Boolean= false
    companion object{
        var currenttheme = String()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       _binding = FragmentThemesBinding.inflate(inflater,container,false)
        val pref = this.activity?.getSharedPreferences("smartbar",Context.MODE_PRIVATE)
        currenttheme = pref?.getString("theme","0").toString()
        themescheck()
        return binding?.root
    }
     fun themescheck() {
        tempArrayList = arrayListOf()
        newArrayList = arrayListOf()
        tempArrayList.addAll(List_themes.themes)
        newArrayList.addAll(tempArrayList)
        _recyclerview = binding?.themerecycler
         val adopter = themeAdopter(this.requireContext(),List_themes.themes,object :themeAdopter.onItemClickListener{
             override fun onItemClick(Item: Themes, position: Int) {
                 saveSharedpref(position)
             }
         })
         _recyclerview?.adapter = adopter
         _recyclerview?.setHasFixedSize(true)
         _recyclerview?.layoutManager = LinearLayoutManager(this.requireContext())
         _recyclerview?.layoutManager = GridLayoutManager(requireContext(),2)
    }
    fun saveSharedpref(position: Int){
        val sharedpref = activity?.getSharedPreferences("smartbar",Context.MODE_PRIVATE)
        val editor = sharedpref?.edit()
        editor?.putString("theme",position.toString())
        editor?.apply()
        if(currenttheme.toInt() ==position){
            if(toast==false) {
                Toast.makeText(requireContext(), "Themes already Applied", Toast.LENGTH_SHORT)
                    .show()
                toast = true
                vibrate()
            }
        }
        else{

           // Toast.makeText(requireContext(), "Theme Applied", Toast.LENGTH_SHORT).show()
            vibrate()
            if (MusicActivity.musicService!=null){
                if(MusicActivity.isplaying==false){
                    MusicActivity.musicService!!.stopForeground(true)
                    MusicActivity.musicService = null
                    MusicActivity.isplaying=false
                }
            }
            val intent = Intent(this.requireContext(), MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
            InterstialAdsCall.getInstance().showInterstitialAdNew(requireActivity())
        }
    }
    private fun vibrate() {
        val vibrator = activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
    }
}