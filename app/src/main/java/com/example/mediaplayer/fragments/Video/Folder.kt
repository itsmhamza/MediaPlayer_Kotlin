package com.example.mediaplayer.fragments.Video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentFolderBinding


class folder : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_folder, container, false)
        val binding = FragmentFolderBinding.bind(view)
        binding.folderRecyclerview.setHasFixedSize(true)
        binding.folderRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.folderRecyclerview.adapter = FolderRAdopter(requireContext(), Video.folderList)
        return view
    }
}