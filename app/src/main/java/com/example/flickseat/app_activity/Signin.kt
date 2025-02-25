package com.example.flickseat.app_activity

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.content.Intent
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.flickseat.R
import com.example.flickseat.database.ApiResponse
import com.example.flickseat.database.ApiService
import com.example.flickseat.database.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Signin : AppCompatActivity() {

    private var isPasswordVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signin)

        val signInBtn = findViewById<Button>(R.id.SignIn_button)
        signInBtn.setOnClickListener {
            performSignIn()
        }

        val txtSignin = findViewById<TextView>(R.id.txt_signin)
        txtSignin.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
            finish()
        }

        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val passwordToggle = findViewById<ImageView>(R.id.password_toggle)

        passwordToggle.setOnClickListener {
            if (isPasswordVisible) {
                // Hide password
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                passwordToggle.setImageResource(R.drawable.closed_eye) // closed eye drawable
            } else {
                // Show password
                etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                passwordToggle.setImageResource(R.drawable.open_eye) // open eye drawable
            }
            isPasswordVisible = !isPasswordVisible
            etPassword.setSelection(etPassword.text?.length ?: 0)
        }
    }

    private fun performSignIn() {
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)

        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validate that the fields are not empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_LONG).show()
            return
        }

        val apiService: ApiService = RetrofitClient.instance

        apiService.signIn(email, password).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@Signin, "Login successful", Toast.LENGTH_LONG).show()
                    // Navigate to Botnav (or your main app screen)
                    val intent = Intent(this@Signin, Botnav::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@Signin,
                        response.body()?.message ?: "Login failed.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@Signin, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}