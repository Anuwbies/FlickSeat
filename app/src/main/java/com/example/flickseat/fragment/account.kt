package com.example.flickseat.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.flickseat.R
import com.example.flickseat.app_activity.MainActivity
import com.example.flickseat.database.ApiService
import com.example.flickseat.database.RetrofitClient
import com.example.flickseat.database.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class account : Fragment() {

    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var profilePic: TextView
    private lateinit var container: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        // Initialize Views
        tvUsername = view.findViewById(R.id.tvUsername)
        tvEmail = view.findViewById(R.id.tvEmail)
        profilePic = view.findViewById(R.id.profile_pic)
        this.container = view.findViewById(R.id.container)

        setRandomBackgroundColor()

        // Get user ID from SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        if (userId != -1) {
            fetchUserDetails(userId)
        } else {
            Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_LONG).show()
        }

        val btnLogout: Button = view.findViewById(R.id.btnLog_out)
        btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        return view
    }

    private fun fetchUserDetails(userId: Int) {
        val apiService: ApiService = RetrofitClient.instance
        apiService.getUserDetails(userId).enqueue(object : Callback<UserResponse> {
            @SuppressLint("DiscouragedApi")
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val user = response.body()?.user
                    if (user != null) {
                        tvUsername.text = user.username
                        tvEmail.text = user.email

                        // Display first two letters of username in profilePic TextView
                        profilePic.text = user.username.take(2).uppercase()

                        // Set text color and background color
                        profilePic.setTextColor(Color.WHITE)
                        setRandomBackgroundColor()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load user data.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showLogoutConfirmationDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_box, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        // Show dialog first to get Window attributes
        dialog.show()

        // Force dialog width to match the screen width
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),  // 85% of screen width
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Make background transparent for rounded corners effect
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Initialize buttons from dialog_box.xml
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnLogout = dialogView.findViewById<Button>(R.id.btnLogout)

        btnCancel.setOnClickListener {
            dialog.dismiss() // Close the dialog
        }

        btnLogout.setOnClickListener {
            dialog.dismiss() // Close the dialog
            performLogout()  // Call logout function
        }
    }

    private fun performLogout() {
        // Clear user data from SharedPreferences
        sharedPreferences.edit().clear().apply()

        // Show a toast message for successful logout
        Toast.makeText(requireContext(), "Logout successful!", Toast.LENGTH_SHORT).show()

        // Navigate to MainActivity
        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setRandomBackgroundColor() {
        // Generate moderately light colors (values between 100 and 220)
        val red = Random.nextInt(100, 220)
        val green = Random.nextInt(100, 220)
        val blue = Random.nextInt(100, 220)

        val balancedLightColor = Color.rgb(red, green, blue)
        container.setColorFilter(balancedLightColor)
    }
}