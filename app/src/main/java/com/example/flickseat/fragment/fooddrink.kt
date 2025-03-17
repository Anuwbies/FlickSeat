package com.example.flickseat.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.flickseat.R
import com.example.flickseat.database.RetrofitClient
import com.example.flickseat.database.Ticket
import com.example.flickseat.database.UserTicketResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class fooddrink : Fragment() {

    private lateinit var tvPleaseBookSeatFirst: TextView
    private var ticketList: MutableList<Ticket> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fooddrink, container, false)

        tvPleaseBookSeatFirst = view.findViewById(R.id.tvPleasebookseatfirst)

        fetchUserTickets()
        return view
    }

    private fun fetchUserTickets() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserData", 0)
        val userId = sharedPreferences.getInt("user_id", 0)

        if (userId == 0) {
            Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
            tvPleaseBookSeatFirst.visibility = View.VISIBLE
            return
        }

        RetrofitClient.instance.getUserTickets(userId).enqueue(object : Callback<UserTicketResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<UserTicketResponse>, response: Response<UserTicketResponse>) {
                if (response.isSuccessful) {
                    val ticketResponse = response.body()
                    ticketList.clear()
                    ticketList.addAll(ticketResponse?.tickets ?: emptyList())

                    // Show "Please book a seat first" if there are no tickets
                    tvPleaseBookSeatFirst.visibility = if (ticketList.isEmpty()) View.VISIBLE else View.GONE
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserTicketResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed to load tickets: ${t.message}", Toast.LENGTH_SHORT).show()
                tvPleaseBookSeatFirst.visibility = View.VISIBLE
            }
        })
    }
}