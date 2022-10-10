package com.example.mediaplayer.fragments

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mediaplayer.PlayerActivity
import com.example.mediaplayer.databinding.FragmentSettingBinding


class Setting : Fragment() {

    private var _binding: FragmentSettingBinding?=null
    private val binding get() =_binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingBinding.inflate(inflater,container,false)
        //save orientation
        val edit = activity?.getSharedPreferences("orientation",Context.MODE_PRIVATE)
            ?.getInt("edit",0)
        if (edit ==1){
            binding?.switchOr?.setChecked(true)
            PlayerActivity.orientationn = true
            binding!!.oritentation.text = "Landscape"
        }
        //save popup
        val edit1 = activity?.getSharedPreferences("popup",Context.MODE_PRIVATE)
            ?.getInt("edit1",0)
        if (edit1 ==1){
            binding?.switchPopup?.setChecked(true)
            PlayerActivity.popup = true
        }
        //save 10s
        val status2 =   activity?.getSharedPreferences("tap",Context.MODE_PRIVATE)?.getBoolean("status2",true)
        binding?.switchDouble?.setChecked(status2!!)

        //save gesture
        val edit3 = activity?.getSharedPreferences("gesture",Context.MODE_PRIVATE)
            ?.getInt("edit3",0)
        if (edit3 ==1) {
            binding?.switchgesture?.setChecked(true)
            PlayerActivity.gesture = true
        }
            //save media name
        val status =   activity?.getSharedPreferences("media",Context.MODE_PRIVATE)?.getBoolean("status",true)
        binding?.switchmedia?.setChecked(status!!)

            binding?.privacy?.setOnClickListener {
                val uri =
                    Uri.parse("https://privacypolicymobirzone.blogspot.com/2022/04/privacy-policy-of-mobir-zone.html")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            binding?.materialCardView?.setOnClickListener {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + context?.packageName)))
                }
                catch (e: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + context?.packageName)))
                }
            }
            binding?.switchOr?.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    activity?.getSharedPreferences("orientation", Context.MODE_PRIVATE)?.edit()
                        ?.putInt("edit", 1)?.apply()
                    PlayerActivity.orientationn = true
                    binding!!.oritentation.text = "Landscape"
                } else {
                    activity?.getSharedPreferences("orientation", Context.MODE_PRIVATE)?.edit()
                        ?.putInt("edit", 0)?.apply()
                    PlayerActivity.orientationn = false
                    binding!!.oritentation.text = "Potrait"

                }
            }
            binding?.switchPopup?.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    activity?.getSharedPreferences("popup", Context.MODE_PRIVATE)?.edit()
                        ?.putInt("edit1", 1)?.apply()
                    PlayerActivity.popup = true
                } else {
                    activity?.getSharedPreferences("popup", Context.MODE_PRIVATE)?.edit()
                        ?.putInt("edit1", 0)?.apply()
                    PlayerActivity.popup = false
                }
            }
            binding?.switchDouble?.setOnCheckedChangeListener { buttonView, isChecked ->
                activity?.getSharedPreferences("tap",Context.MODE_PRIVATE)?.edit()
                    ?.putBoolean("status2",isChecked)?.apply()
                Log.e(TAG, "$isChecked" )

            }
            binding?.switchgesture?.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    activity?.getSharedPreferences("gesture", Context.MODE_PRIVATE)?.edit()
                        ?.putInt("edit3", 1)?.apply()
                    PlayerActivity.gesture = true
                } else {
                    activity?.getSharedPreferences("gesture", Context.MODE_PRIVATE)?.edit()
                        ?.putInt("edit3", 0)?.apply()
                    PlayerActivity.gesture = false
                }
            }
            binding?.switchmedia?.setOnCheckedChangeListener { buttonView, isChecked ->
                activity?.getSharedPreferences("media",Context.MODE_PRIVATE)?.edit()
                    ?.putBoolean("status",isChecked)?.apply()
                Log.e(TAG, "$isChecked" )

            }
        return binding?.root
    }
}