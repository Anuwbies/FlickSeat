package com.example.flickseat.app_activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.flickseat.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        if (isLoggedIn) {
            // ✅ User is logged in, go to Botnav
            startActivity(Intent(this, Botnav::class.java))
            finish() // ✅ Close MainActivity
        }

        val signInBtn = findViewById<Button>(R.id.SignIn_button)
        signInBtn.setOnClickListener {
            val intent = Intent(this, Signin::class.java)
            startActivity(intent)
        }

        val signUpBtn = findViewById<Button>(R.id.SignUp_button)
        signUpBtn.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }
    }
}