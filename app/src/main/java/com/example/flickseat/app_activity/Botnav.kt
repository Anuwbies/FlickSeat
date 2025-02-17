package com.example.flickseat.app_activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flickseat.R
import com.example.flickseat.account
import com.example.flickseat.home
import com.example.flickseat.myticket
import nl.joery.animatedbottombar.AnimatedBottomBar

class Botnav : AppCompatActivity() {
    private lateinit var bottomBar: AnimatedBottomBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_botnav)

        bottomBar = findViewById(R.id.bottom_bar)

        if (savedInstanceState == null) {
            replaceFragment(home())
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
                    R.id.tab_account -> account()
                    else -> home()
                }
                replaceFragment(selectedFragment)
            }

            override fun onTabReselected(index: Int, tab: AnimatedBottomBar.Tab) {}
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}