package com.wings.mile.firebase

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class AuthPagerAdapter (
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {

    private var mTabCount = 0

    fun setCount(count: Int) {
        mTabCount = count
    }

    override fun getItemCount(): Int {
        return mTabCount
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PhoneVerificationFragment.newInstance()
            1 -> OtpVerificationFragment.newInstance()
            else -> PhoneVerificationFragment.newInstance()
        }
    }
}