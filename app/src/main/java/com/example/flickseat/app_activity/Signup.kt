package com.example.flickseat.app_activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.flickseat.R

class Signup : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        val signUp_btn = findViewById<Button>(R.id.SignUp_button)
        signUp_btn.setOnClickListener {
            val intent = Intent(this, Signin::class.java)
            startActivity(intent)
            finish()
        }

        val txt_signup = findViewById<TextView>(R.id.txt_signup)
        txt_signup.setOnClickListener {
            val intent = Intent(this, Signin::class.java)
            startActivity(intent)
            finish()
        }
    }
}