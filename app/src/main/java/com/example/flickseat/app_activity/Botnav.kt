package com.example.flickseat.app_activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flickseat.R
import com.example.flickseat.fragment.account
import com.example.flickseat.fragment.fooddrink
import com.example.flickseat.fragment.home
import com.example.flickseat.fragment.myticket
import nl.joery.animatedbottombar.AnimatedBottomBar

class Botnav : AppCompatActivity() {
    private lateinit var bottomBar: AnimatedBottomBar
    private var lastIndex = 0 // Track last selected tab index

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_botnav)

        bottomBar = findViewById(R.id.bottom_bar)

        if (savedInstanceState == null) {
            replaceFragment(home(), false) // No animation on first launch
        }

        bottomBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                val selectedFragment: Fragment = when (newTab.id) {
                    R.id.tab_myTicket -> myticket()
                    R.id.tab_fooddrink -> fooddrink()
                    R.id.tab_account -> account()
                    else -> home()
                }
                replaceFragment(selectedFragment, newIndex > this@Botnav.lastIndex)
                this@Botnav.lastIndex = newIndex // Update last index
            }

            override fun onTabReselected(index: Int, tab: AnimatedBottomBar.Tab) {}
        })
    }

    private fun replaceFragment(fragment: Fragment, slideLeft: Boolean) {
        val enterAnim = if (slideLeft) R.anim.slide_in_right else R.anim.slide_in_left
        val exitAnim = if (slideLeft) R.anim.slide_out_left else R.anim.slide_out_right

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(enterAnim, exitAnim)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}