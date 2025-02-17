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

        val signIn_btn = findViewById<Button>(R.id.SignIn_button)

        signIn_btn.setOnClickListener {
            val intent = Intent(this, Signin::class.java)
            startActivity(intent)
        }

        val signUp_btn = findViewById<Button>(R.id.SignUp_button)

        signUp_btn.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }
    }

}