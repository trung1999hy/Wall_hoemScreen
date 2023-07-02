package com.example.backgroundimage.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MainViewPager(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {
    private var listFragment: ArrayList<Fragment> = arrayListOf()
    override fun getCount(): Int {
        return listFragment.size
    }

    override fun getItem(position: Int): Fragment {
        return listFragment.get(position)
    }

    fun setData(listFragment: ArrayList<Fragment>) {
        this.listFragment = listFragment
        notifyDataSetChanged()
    }
}