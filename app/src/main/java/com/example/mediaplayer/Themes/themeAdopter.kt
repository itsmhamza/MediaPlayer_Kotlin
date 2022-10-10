package com.example.mediaplayer.Themes

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mediaplayer.R
import java.util.*
import kotlin.collections.ArrayList

class themeAdopter(private val context: Context, private val language_list:ArrayList<com.example.mediaplayer.Data.Themes>,
                 private val listener: onItemClickListener
):RecyclerView.Adapter<themeAdopter.MyViewHolder>(),Filterable {

    private lateinit var mlistener: onItemClickListener
    var copycountrieslist = ArrayList<com.example.mediaplayer.Data.Themes>()
    init {
        copycountrieslist=language_list
        mlistener=listener
    }
    interface onItemClickListener {
        fun onItemClick(Item:com.example.mediaplayer.Data.Themes,position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.themelist,parent,false)
        return MyViewHolder(itemView,mlistener)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currrentitem = copycountrieslist[position]
        Glide.with(context)
            .load(currrentitem.titleImage)
            .apply(RequestOptions().placeholder(R.color.bg_color).centerCrop())
            .into(holder.titleimage)

        if (Themes.currenttheme.toInt() ==position){
            holder.frame.visibility = View.VISIBLE
            holder.done.visibility = View.VISIBLE
            if (Themes.currenttheme.toInt()==0){
                holder.frame.setColorFilter(ContextCompat.getColor(context,R.color.white),android.graphics.PorterDuff.Mode.MULTIPLY)
                holder.done.setColorFilter(ContextCompat.getColor(context,R.color.white),android.graphics.PorterDuff.Mode.MULTIPLY)
            }
        }
        else{
            holder.frame.visibility = View.GONE
            holder.done.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            Log.e("check","hello")
            mlistener.onItemClick(currrentitem,position)
        }
    }

    override fun getItemCount(): Int {
        return copycountrieslist.size
    }
//    fun setOnitemClickListener(listener: onItemClickListener){
//        mlistener = listener
//    }
    class MyViewHolder(itemView: View,listener: onItemClickListener):RecyclerView.ViewHolder(itemView){
        var titleimage:ImageView=itemView.findViewById(R.id.title_image2)
        var frame:ImageView = itemView.findViewById(R.id.frame)
    var done:ImageView = itemView.findViewById(R.id.done)

    }
    override fun getFilter(): Filter {
        return object :Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charsearch = constraint.toString()
                if (charsearch.isEmpty()){
                    copycountrieslist = language_list
                }else{
                    val resultlist = ArrayList<com.example.mediaplayer.Data.Themes>()
                    for (row in language_list){
                        if (row.heading.lowercase(Locale.ROOT)
                                .startsWith(charsearch.lowercase(Locale.ROOT))){
                        resultlist.add(row)
                        }
                    }
                    copycountrieslist=resultlist
                }
                val filterresults = FilterResults()
                filterresults.values =copycountrieslist
                return filterresults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                copycountrieslist =results?.values as ArrayList<com.example.mediaplayer.Data.Themes>
                notifyDataSetChanged()
            }

        }
    }

}