package com.example.flickseat.app_activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.text.method.HideReturnsTransformationMethod
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.view.inputmethod.EditorInfo
import android.view.KeyEvent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.flickseat.R
import com.example.flickseat.database.UserResponse
import com.example.flickseat.database.ApiService
import com.example.flickseat.database.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Signup : AppCompatActivity() {

    private var isPasswordVisible: Boolean = false
    private var isConfirmPasswordVisible: Boolean = false
    private val TAG = "SignupActivity"

    private val passwordRegex = Regex("^(?=.*[0-9]).{8,16}\$")

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        val txtSignup = findViewById<TextView>(R.id.txt_signup)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirm_password)
        val passwordToggle = findViewById<ImageView>(R.id.password_toggle)
        val confirmPasswordToggle = findViewById<ImageView>(R.id.confirm_password_toggle)
        val signUpBtn = findViewById<Button>(R.id.SignUp_button)

        txtSignup.setOnClickListener {
            val intent = Intent(this, Signin::class.java)
            startActivity(intent)
            finish()
        }

        passwordToggle.setOnClickListener {
            togglePasswordVisibility(etPassword, passwordToggle, isPasswordVisible)
            isPasswordVisible = !isPasswordVisible
        }

        confirmPasswordToggle.setOnClickListener {
            togglePasswordVisibility(etConfirmPassword, confirmPasswordToggle, isConfirmPasswordVisible)
            isConfirmPasswordVisible = !isConfirmPasswordVisible
        }

        signUpBtn.setOnClickListener {
            validateAndSignUp(etUsername, etEmail, etPassword, etConfirmPassword)
        }

        // ✅ Pressing "Enter" on Confirm Password Field triggers sign-up
        etConfirmPassword.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                validateAndSignUp(etUsername, etEmail, etPassword, etConfirmPassword)
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun validateAndSignUp(
        etUsername: EditText,
        etEmail: EditText,
        etPassword: EditText,
        etConfirmPassword: EditText
    ) {
        val username = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_LONG).show()
            return
        }
        if (username.length < 3 || username.length > 8) {
            Toast.makeText(this, "Username must be between 3 and 8 characters.", Toast.LENGTH_LONG).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_LONG).show()
            return
        }
        if (!passwordRegex.matches(password)) {
            Toast.makeText(
                this,
                "Password must be 8-16 characters and contain at least one number.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_LONG).show()
            return
        }

        signUpUser(email, username, password)
    }

    private fun signUpUser(email: String, username: String, password: String) {
        val apiService: ApiService = RetrofitClient.instance

        apiService.signUp(email, username, password)
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d(TAG, "Response: $responseBody")

                        if (responseBody?.status == "success") {
                            Toast.makeText(this@Signup, "User registered successfully!", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@Signup, Signin::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.e(TAG, "Signup failed: ${responseBody?.message}")
                            Toast.makeText(this@Signup, responseBody?.message ?: "Registration failed.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Log.e(TAG, "Server returned an error: ${response.errorBody()?.string()}")
                        Toast.makeText(this@Signup, "Registration failed: ${response.message()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.e(TAG, "Network error: ${t.message}", t)
                    Toast.makeText(this@Signup, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun togglePasswordVisibility(editText: EditText, toggleIcon: ImageView, isVisible: Boolean) {
        if (isVisible) {
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            toggleIcon.setImageResource(R.drawable.closed_eye)
        } else {
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            toggleIcon.setImageResource(R.drawable.open_eye)
        }
        editText.setSelection(editText.text?.length ?: 0)
    }
}