package com.example.flickseat.app_activity

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.content.Intent
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.view.inputmethod.EditorInfo
import android.view.KeyEvent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.flickseat.R
import com.example.flickseat.database.ApiService
import com.example.flickseat.database.RetrofitClient
import com.example.flickseat.database.UserResponse
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SigninActivity : AppCompatActivity() {

    private var isPasswordVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signin)

        val signInBtn = findViewById<Button>(R.id.SignIn_button)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val txtSignin = findViewById<TextView>(R.id.txt_signin)
        val passwordToggle = findViewById<ImageView>(R.id.password_toggle)

        signInBtn.setOnClickListener {
            performSignIn()
        }

        txtSignin.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }

        passwordToggle.setOnClickListener {
            if (isPasswordVisible) {
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                passwordToggle.setImageResource(R.drawable.closed_eye)
            } else {
                etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                passwordToggle.setImageResource(R.drawable.open_eye)
            }
            isPasswordVisible = !isPasswordVisible
            etPassword.setSelection(etPassword.text?.length ?: 0)
        }

        // ✅ Pressing "Enter" triggers sign-in
        etPassword.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                performSignIn()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun performSignIn() {
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)

        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_LONG).show()
            return
        }

        val apiService: ApiService = RetrofitClient.instance

        apiService.signIn(email, password).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val userId = response.body()?.user?.user_id ?: -1
                    saveUserSession(userId)

                    Toast.makeText(this@SigninActivity, "Login successful", Toast.LENGTH_LONG).show()

                    // ✅ Clear previous activities and start Botnav
                    val intent = Intent(this@SigninActivity, Botnav::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@SigninActivity,
                        response.body()?.message ?: "Login failed.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(this@SigninActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    // ✅ Save User Session
    private fun saveUserSession(userId: Int) {
        val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("user_id", userId)
        editor.putBoolean("is_logged_in", true)
        editor.apply()
    }
}