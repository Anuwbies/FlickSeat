package com.example.flickseat.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flickseat.R
import com.example.flickseat.adapter.TicketAdapter
import com.example.flickseat.database.RetrofitClient
import com.example.flickseat.database.Ticket
import com.example.flickseat.database.UserTicketResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class myticket : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var ticketAdapter: TicketAdapter
    private var ticketList: MutableList<Ticket> = mutableListOf()
    private lateinit var tvNoTicketFound: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_myticket, container, false)

        recyclerView = view.findViewById(R.id.ticketsRV)
        tvNoTicketFound = view.findViewById(R.id.tvNoticketfound)
        val btnFilter: View = view.findViewById(R.id.btnFilter) // Find the sort button

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        ticketAdapter = TicketAdapter(ticketList)
        recyclerView.adapter = ticketAdapter

        // Show PopupMenu when clicking the sort button
        btnFilter.setOnClickListener { showSortMenu(it) }

        fetchUserTickets()
        return view
    }

    private fun fetchUserTickets() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserData", 0)
        val userId = sharedPreferences.getInt("user_id", 0)

        if (userId == 0) {
            Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("MyTicketFragment", "Fetching tickets for user_id: $userId") // Debug log

        RetrofitClient.instance.getUserTickets(userId).enqueue(object : Callback<UserTicketResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<UserTicketResponse>, response: Response<UserTicketResponse>) {
                Log.d("MyTicketFragment", "Response code: ${response.code()}")
                Log.d("MyTicketFragment", "Response body: ${response.body()}")

                if (response.isSuccessful) {
                    val ticketResponse = response.body()
                    if (ticketResponse?.status == "success") {
                        ticketList.clear()
                        ticketList.addAll(ticketResponse.tickets ?: emptyList())
                        ticketAdapter.notifyDataSetChanged()

                        tvNoTicketFound.visibility = if (ticketList.isEmpty()) View.VISIBLE else View.GONE
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserTicketResponse>, t: Throwable) {
                Log.e("MyTicketFragment", "API call failed", t)
                Toast.makeText(requireContext(), "Failed to load tickets: ${t.message}", Toast.LENGTH_SHORT).show()

                // Show "No tickets found" if the API call fails
                tvNoTicketFound.visibility = View.VISIBLE
            }
        })
    }

    // Function to show PopupMenu for sorting
    private fun showSortMenu(view: View) {
        val popupMenu = android.widget.PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.filter_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.filter_movie -> sortTickets("Movie")
                R.id.filter_day -> sortTickets("Day")
                R.id.filter_time -> sortTickets("Time")
                R.id.filter_status -> sortTickets("Status")
            }
            true
        }
        popupMenu.show()
    }

    // Function to sort tickets
    @SuppressLint("NotifyDataSetChanged")
    private fun sortTickets(sortType: String) {
        when (sortType) {
            "Movie" -> ticketList.sortBy { it.movie_title } // Sort by movie name (A-Z)

            "Day" -> {
                // Define custom order for days of the week
                val dayOrder = mapOf(
                    "Mon" to 1,
                    "Tue" to 2,
                    "Wed" to 3,
                    "Thu" to 4,
                    "Fri" to 5
                )

                // Sort tickets based on the custom day order
                ticketList.sortBy { dayOrder[it.show_day] ?: Int.MAX_VALUE }
            }

            "Time" -> {
                // Define custom order for show times
                val timeOrder = mapOf(
                    "10:00am" to 1,
                    "12:00pm" to 2,
                    "4:00pm"  to 3,
                    "8:00pm"  to 4,
                    "12:00am" to 5
                )

                // Sort tickets based on the custom time order
                ticketList.sortBy { timeOrder[it.show_time] ?: Int.MAX_VALUE }
            }

            "Status" -> ticketList.sortBy { it.status } // Sort by status alphabetically
        }

        ticketAdapter.notifyDataSetChanged() // Refresh RecyclerView after sorting
    }
}
