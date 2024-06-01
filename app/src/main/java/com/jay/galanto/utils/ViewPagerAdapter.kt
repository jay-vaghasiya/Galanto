package com.jay.galanto.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jay.galanto.ui.fragment.dashboard.MCPFragment
import com.jay.galanto.ui.fragment.dashboard.PIPFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MCPFragment()
            else -> PIPFragment()
        }
    }
}