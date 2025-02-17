package com.example.flickseat.app_activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.flickseat.R

class Signin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signin)

        val signIn_btn = findViewById<Button>(R.id.SignIn_button)
        signIn_btn.setOnClickListener {
            val intent = Intent(this, Botnav::class.java)
            startActivity(intent)
            finish()
        }

        val txt_signin = findViewById<TextView>(R.id.txt_signin)
        txt_signin.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
            finish()
        }
    }
}