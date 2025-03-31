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
import com.example.flickseat.adapter.StatusAdapter
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
    private lateinit var statusRecyclerView: RecyclerView
    private lateinit var statusAdapter: StatusAdapter
    private lateinit var tvNoTicketFound: TextView

    private var ticketList: MutableList<Ticket> = mutableListOf()
    private var filteredTicketList: MutableList<Ticket> = mutableListOf()
    private val statusList: List<String> = listOf("All", "pending", "confirmed", "cancelled", "used")
        .map { it.replaceFirstChar { char -> char.uppercase() } }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_myticket, container, false)

        // Initialize UI components
        recyclerView = view.findViewById(R.id.ticketsRV)
        statusRecyclerView = view.findViewById(R.id.statusRV)
        tvNoTicketFound = view.findViewById(R.id.tvNoticketfound)

        // Setup Status RecyclerView
        statusRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        statusAdapter = StatusAdapter(statusList) { selectedStatus -> filterTickets(selectedStatus) }
        statusRecyclerView.adapter = statusAdapter

        // Setup Tickets RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        ticketAdapter = TicketAdapter(filteredTicketList)
        recyclerView.adapter = ticketAdapter

        statusRecyclerView.visibility = View.GONE
        tvNoTicketFound.visibility = View.VISIBLE

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

        Log.d("MyTicketFragment", "Fetching tickets for user_id: $userId")

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

                        // Sort tickets by latest purchase_date first
                        ticketList.sortByDescending { it.purchase_date }

                        if (ticketList.isEmpty()) {
                            // Hide statusRecyclerView if no tickets are fetched initially
                            statusRecyclerView.visibility = View.GONE
                            tvNoTicketFound.visibility = View.VISIBLE
                        } else {
                            // Show statusRecyclerView only if tickets exist
                            statusRecyclerView.visibility = View.VISIBLE
                            tvNoTicketFound.visibility = View.GONE
                            filterTickets("All")  // Show all tickets initially
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserTicketResponse>, t: Throwable) {
                Log.e("MyTicketFragment", "API call failed", t)
                Toast.makeText(requireContext(), "Failed to load tickets: ${t.message}", Toast.LENGTH_SHORT).show()

                tvNoTicketFound.visibility = View.VISIBLE
                statusRecyclerView.visibility = View.GONE
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterTickets(status: String) {
        filteredTicketList.clear()

        if (status == "All") {
            filteredTicketList.addAll(ticketList)
        } else {
            filteredTicketList.addAll(ticketList.filter { it.status.equals(status, ignoreCase = true) })
        }

        ticketAdapter.notifyDataSetChanged()
        tvNoTicketFound.visibility = if (filteredTicketList.isEmpty()) View.VISIBLE else View.GONE
    }
}
