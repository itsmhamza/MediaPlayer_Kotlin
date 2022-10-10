package com.example.mediaplayer.fragments.Video

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.Data.folderr
import com.example.mediaplayer.databinding.FolderViewBinding

class FolderRAdopter(private val content: Context, private var folderlist: ArrayList<folderr>):
    RecyclerView.Adapter<FolderRAdopter.MyHolder>() {
    class MyHolder(binding: FolderViewBinding ): RecyclerView.ViewHolder(binding.root) {
        val foldername = binding.folderNameFV
        val root = binding.root
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(FolderViewBinding.inflate(LayoutInflater.from(content),parent,false))

    }
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
       holder.foldername.text = folderlist[position].folderName
        holder.root.setOnClickListener {
            val intent  = Intent(content,folderActivity::class.java)
            intent.putExtra("position",position)
            ContextCompat.startActivity(content,intent,null)
        }

    }
    override fun getItemCount(): Int {
        return folderlist.size
    }
}
