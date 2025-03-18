package com.example.flickseat.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flickseat.R
import com.example.flickseat.adapter.FoodDrinkAdapter
import com.example.flickseat.database.FoodDrinkResponse
import com.example.flickseat.database.RetrofitClient
import com.example.flickseat.database.Ticket
import com.example.flickseat.database.UserTicketResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class fooddrink : Fragment() {

    private lateinit var tvPleaseBookSeatFirst: TextView
    private var ticketList: MutableList<Ticket> = mutableListOf()

    private lateinit var foodsRV: RecyclerView
    private lateinit var drinksRV: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fooddrink, container, false)

        tvPleaseBookSeatFirst = view.findViewById(R.id.tvPleasebookseatfirst)

        foodsRV = view.findViewById(R.id.foodsRV)
        drinksRV = view.findViewById(R.id.drinksRV)

        foodsRV.layoutManager = LinearLayoutManager(requireContext())
        drinksRV.layoutManager = LinearLayoutManager(requireContext())

        fetchFoodDrinks()

        fetchUserTickets()
        return view
    }

    private fun fetchFoodDrinks() {
        RetrofitClient.instance.getFoodsDrinks().enqueue(object : Callback<FoodDrinkResponse> {
            override fun onResponse(call: Call<FoodDrinkResponse>, response: Response<FoodDrinkResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val foodDrinkResponse = response.body()

                    val foodList = foodDrinkResponse?.foods ?: emptyList()
                    val drinkList = foodDrinkResponse?.drinks ?: emptyList()

                    // ✅ Pass isDrinkList = true for drinks, false for food
                    foodsRV.adapter = FoodDrinkAdapter(requireContext(), foodList, isDrinkList = false)
                    drinksRV.adapter = FoodDrinkAdapter(requireContext(), drinkList, isDrinkList = true)
                } else {
                    Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FoodDrinkResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed to load: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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