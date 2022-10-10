package com.example.mediaplayer.fragments.Music

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class audioAdopter(supportFragmentManager: FragmentManager): FragmentPagerAdapter(supportFragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    private val mFragmentlist= ArrayList<Fragment>()
    private val mfragmenttitle = ArrayList<String>()

    override fun getCount(): Int {
        return mFragmentlist.size
    }

    override fun getItem(position: Int): Fragment {
        return mFragmentlist[position]

    }
    fun getpagetitle(position: Int): String {
        return mfragmenttitle.get(position)
    }
    fun addFragment(fragment: Fragment, title:String){
        mFragmentlist.add(fragment)
        mfragmenttitle.add(title)

    }
}