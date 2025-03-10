package com.example.flickseat.fragment

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.flickseat.R
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
    private lateinit var profilePic: ImageView
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

        return view
    }

    private fun fetchUserDetails(userId: Int) {
        val apiService: ApiService = RetrofitClient.instance
        apiService.getUserDetails(userId).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val user = response.body()?.user
                    if (user != null) {
                        tvUsername.text = user.username
                        tvEmail.text = user.email

                        // Set profile picture dynamically from drawable
                        val resourceId = resources.getIdentifier(user.profile_pic, "drawable", requireActivity().packageName)
                        if (resourceId != 0) {
                            profilePic.setImageResource(resourceId)
                        } else {
                            profilePic.setImageResource(R.drawable.person) // Default profile pic
                        }

                        profilePic.setColorFilter(Color.WHITE)
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

    private fun setRandomBackgroundColor() {
        val randomColor = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        container.setColorFilter(randomColor) // ✅ Keeps the circular shape intact
    }
}