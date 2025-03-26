package com.example.flickseat.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flickseat.R
import com.example.flickseat.adapter.FoodDrinkAdapter
import com.example.flickseat.adapter.OrderAdapter
import com.example.flickseat.database.FoodDrink
import com.example.flickseat.database.FoodDrinkResponse
import com.example.flickseat.database.Order
import com.example.flickseat.database.OrderResponse
import com.example.flickseat.database.OrderedResponse
import com.example.flickseat.database.RetrofitClient
import com.example.flickseat.database.Ticket
import com.example.flickseat.database.UserTicketResponse
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class fooddrink : Fragment(), FoodDrinkAdapter.OnTotalPriceChangeListener {

    private lateinit var tvPleaseBookSeatFirst: TextView
    private lateinit var tvFoods: TextView
    private lateinit var tvDrinks: TextView
    private lateinit var tvTotalPrice: TextView
    private lateinit var foodsRV: RecyclerView
    private lateinit var drinksRV: RecyclerView
    private lateinit var btnPrcdtoPayment: Button

    private var foodTotalPrice = 0
    private var drinkTotalPrice = 0

    private var ticketList: MutableList<Ticket> = mutableListOf()
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var selectedPaymentMethod: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fooddrink, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvPleaseBookSeatFirst = view.findViewById(R.id.tvPleasebookseatfirst)
        tvFoods = view.findViewById(R.id.tvFoods)
        tvDrinks = view.findViewById(R.id.tvDrinks)
        tvTotalPrice = view.findViewById(R.id.tvAllItemTotalPrice) // Added total price TextView
        foodsRV = view.findViewById(R.id.foodsRV)
        drinksRV = view.findViewById(R.id.drinksRV)
        btnPrcdtoPayment = view.findViewById(R.id.btnPrcdtoPayment)

        foodsRV.layoutManager = LinearLayoutManager(requireContext())
        drinksRV.layoutManager = LinearLayoutManager(requireContext())

        fetchFoodDrinks()
        fetchUserTickets()

        btnPrcdtoPayment.setOnClickListener {
            showPaymentBottomSheet()
        }

        val imgOrders = view.findViewById<ImageView>(R.id.imgOrders)

        imgOrders.setOnClickListener {
            val sharedPreferences = requireActivity().getSharedPreferences("UserData", 0)
            val userId = sharedPreferences.getInt("user_id", 0)

            if (userId == 0) {
                Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            RetrofitClient.instance.getOrders(userId).enqueue(object : Callback<OrderedResponse> {
                override fun onResponse(call: Call<OrderedResponse>, response: Response<OrderedResponse>) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        val orders = response.body()?.orders ?: emptyList()
                        if (orders.isEmpty()) {
                            Toast.makeText(requireContext(), "No orders found.", Toast.LENGTH_SHORT).show()
                        } else {
                            showOrdersDialog(orders) // Pass the orders list to the function
                        }
                    } else {
                        Toast.makeText(requireContext(), "No orders found.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<OrderedResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Failed to load orders: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun fetchFoodDrinks() {
        RetrofitClient.instance.getFoodsDrinks().enqueue(object : Callback<FoodDrinkResponse> {
            override fun onResponse(
                call: Call<FoodDrinkResponse>,
                response: Response<FoodDrinkResponse>
            ) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val foodDrinkResponse = response.body()

                    val foodList = foodDrinkResponse?.foods ?: emptyList()
                    val drinkList = foodDrinkResponse?.drinks ?: emptyList()

                    val foodAdapter = FoodDrinkAdapter(requireContext(), foodList, true, this@fooddrink)
                    val drinkAdapter = FoodDrinkAdapter(requireContext(), drinkList, false, this@fooddrink)

                    foodsRV.adapter = foodAdapter
                    drinksRV.adapter = drinkAdapter
                } else {
                    Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<FoodDrinkResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed to load: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun fetchUserTickets() {
        val sharedPreferences = requireActivity().getSharedPreferences("UserData", 0)
        val userId = sharedPreferences.getInt("user_id", 0)

        if (userId == 0) {
            Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
            showOnlyTvPleaseBookSeatFirst()
            return
        }

        RetrofitClient.instance.getUserTickets(userId)
            .enqueue(object : Callback<UserTicketResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<UserTicketResponse>,
                    response: Response<UserTicketResponse>
                ) {
                    if (response.isSuccessful) {
                        val ticketResponse = response.body()
                        ticketList.clear()
                        ticketList.addAll(ticketResponse?.tickets ?: emptyList())

                        if (ticketList.isEmpty()) {
                            showOnlyTvPleaseBookSeatFirst()
                        } else {
                            showAllViews()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        showOnlyTvPleaseBookSeatFirst()
                    }
                }

                override fun onFailure(call: Call<UserTicketResponse>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to load tickets: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    showOnlyTvPleaseBookSeatFirst()
                }
            })
    }

    private fun showOnlyTvPleaseBookSeatFirst() {
        tvPleaseBookSeatFirst.visibility = View.VISIBLE
        tvFoods.visibility = View.GONE
        tvDrinks.visibility = View.GONE
        foodsRV.visibility = View.GONE
        drinksRV.visibility = View.GONE
        tvTotalPrice.visibility = View.GONE
        btnPrcdtoPayment.visibility =View.GONE
    }

    private fun showAllViews() {
        tvPleaseBookSeatFirst.visibility = View.GONE
        tvFoods.visibility = View.VISIBLE
        tvDrinks.visibility = View.VISIBLE
        foodsRV.visibility = View.VISIBLE
        drinksRV.visibility = View.VISIBLE
        tvTotalPrice.visibility = View.VISIBLE
        btnPrcdtoPayment.visibility =View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    override fun onTotalPriceUpdated(foodTotalPrice: Int, drinkTotalPrice: Int) {
        // Update only if a valid new value is provided
        if (foodTotalPrice != -1) this.foodTotalPrice = foodTotalPrice
        if (drinkTotalPrice != -1) this.drinkTotalPrice = drinkTotalPrice

        // Combine food and drink totals
        val combinedTotalPrice = this.foodTotalPrice + this.drinkTotalPrice
        tvTotalPrice.text = "₱ $combinedTotalPrice"
    }

    @SuppressLint("SetTextI18n")
    private fun showPaymentBottomSheet() {
        if (bottomSheetDialog != null && bottomSheetDialog!!.isShowing) {
            return
        }

        bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_payment, null)
        bottomSheetDialog!!.setContentView(view)

        val btnCard = view.findViewById<Button>(R.id.btnCard)
        val btnBank = view.findViewById<Button>(R.id.btnBank)
        val btnGpay = view.findViewById<Button>(R.id.btnGpay)
        val btnGcash = view.findViewById<Button>(R.id.btnGcash)
        val priceTextView = view.findViewById<TextView>(R.id.priceinpayment)
        val btnMakePayment = view.findViewById<Button>(R.id.btnMakePayment)

        val combinedTotalPrice = foodTotalPrice + drinkTotalPrice
        priceTextView.text = "₱ $combinedTotalPrice"

        val paymentButtons = listOf(btnCard, btnBank, btnGpay, btnGcash)

        fun getDrawableFromStart(button: Button): Int {
            return when (button.id) {
                R.id.btnCard -> R.drawable.credit_card
                R.id.btnBank -> R.drawable.online_bank
                R.id.btnGpay -> R.drawable.g_pay
                R.id.btnGcash -> R.drawable.gcash
                else -> 0
            }
        }

        fun selectPaymentButton(selectedButton: Button) {
            selectedPaymentMethod = selectedButton
            for (button in paymentButtons) {
                button.setCompoundDrawablesWithIntrinsicBounds(
                    getDrawableFromStart(button),
                    0,
                    R.drawable.unchecked_radio,
                    0
                )
            }
            selectedButton.setCompoundDrawablesWithIntrinsicBounds(
                getDrawableFromStart(selectedButton),
                0,
                R.drawable.checked_radio,
                0
            )
        }

        selectedPaymentMethod?.let { previouslySelected ->
            selectPaymentButton(
                paymentButtons.firstOrNull { it.id == previouslySelected.id } ?: return@let
            )
        }

        btnCard.setOnClickListener { selectPaymentButton(btnCard) }
        btnBank.setOnClickListener { selectPaymentButton(btnBank) }
        btnGpay.setOnClickListener { selectPaymentButton(btnGpay) }
        btnGcash.setOnClickListener { selectPaymentButton(btnGcash) }

        btnMakePayment.setOnClickListener {
            if (selectedPaymentMethod == null) {
                Toast.makeText(requireContext(), "Please choose a payment method.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPreferences = requireActivity().getSharedPreferences("UserData", 0)
            val userId = sharedPreferences.getInt("user_id", 0)

            if (userId == 0) {
                Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedFoodItems = mutableListOf<Pair<Int, Int>>()
            val selectedDrinkItems = mutableListOf<Pair<Int, Int>>()

            (foodsRV.adapter as? FoodDrinkAdapter)?.let { adapter ->
                for ((index, item) in adapter.itemList.withIndex()) {
                    val quantity = adapter.getItemQuantity(index)
                    if (quantity > 0) {
                        selectedFoodItems.add(Pair(item.id, quantity))
                    }
                }
            }

            (drinksRV.adapter as? FoodDrinkAdapter)?.let { adapter ->
                for ((index, item) in adapter.itemList.withIndex()) {
                    val quantity = adapter.getItemQuantity(index)
                    if (quantity > 0) {
                        selectedDrinkItems.add(Pair(item.id, quantity))
                    }
                }
            }

            if (selectedFoodItems.isEmpty() && selectedDrinkItems.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one item.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val totalOrders = selectedFoodItems.size + selectedDrinkItems.size
            var completedOrders = 0
            var successfulOrders = 0

            fun checkCompletion() {
                completedOrders++
                if (completedOrders == totalOrders) {
                    if (successfulOrders > 0) {
                        Toast.makeText(requireContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show()

                        // Reset the quantities in both adapters
                        (foodsRV.adapter as? FoodDrinkAdapter)?.resetOrder()
                        (drinksRV.adapter as? FoodDrinkAdapter)?.resetOrder()

                        // Reset total price
                        foodTotalPrice = 0
                        drinkTotalPrice = 0
                        tvTotalPrice.text = "₱ 0"
                    } else {
                        Toast.makeText(requireContext(), "Order failed. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                    bottomSheetDialog?.dismiss()
                }
            }

            for ((foodId, quantity) in selectedFoodItems) {
                insertOrder(userId, foodId, null, quantity) { success ->
                    if (success) successfulOrders++
                    checkCompletion()
                }
            }

            for ((drinkId, quantity) in selectedDrinkItems) {
                insertOrder(userId, null, drinkId, quantity) { success ->
                    if (success) successfulOrders++
                    checkCompletion()
                }
            }
        }

        bottomSheetDialog?.setOnDismissListener {
            bottomSheetDialog = null
        }

        bottomSheetDialog?.show()
    }

    private fun insertOrder(userId: Int, foodId: Int?, drinkId: Int?, quantity: Int, onComplete: (Boolean) -> Unit) {
        val foodIdStr = foodId?.toString()
        val drinkIdStr = drinkId?.toString()

        RetrofitClient.instance.insertOrder(userId, foodIdStr, drinkIdStr, quantity)
            .enqueue(object : Callback<OrderResponse> {
                override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                    val responseBody = response.body()
                    if (response.isSuccessful && responseBody?.status == "success") {
                        Log.d("InsertOrder", "Order placed successfully for user $userId")
                        onComplete(true)
                    } else {
                        val errorMsg = responseBody?.message ?: response.errorBody()?.string() ?: "Unknown error"
                        Log.e("InsertOrder", "Order failed: $errorMsg")
                        onComplete(false)
                    }
                }

                override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                    Log.e("InsertOrder", "Request failed: ${t.message}")
                    onComplete(false)
                }
            })
    }

    private fun showOrdersDialog(orders: List<Order>) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialogbox_orders, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val ordersRV = dialogView.findViewById<RecyclerView>(R.id.ordersRV)
        val btnClaimOrder = dialogView.findViewById<Button>(R.id.btnClaimOrder)

        ordersRV.layoutManager = LinearLayoutManager(requireContext())
        val adapter = OrderAdapter(orders)
        ordersRV.adapter = adapter

        btnClaimOrder.setOnClickListener {
            Toast.makeText(requireContext(), "Please proceed to the counter", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun fetchOrders(userId: Int, ordersRV: RecyclerView) {
        RetrofitClient.instance.getOrders(userId).enqueue(object : Callback<OrderedResponse> {
            override fun onResponse(call: Call<OrderedResponse>, response: Response<OrderedResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val orders = response.body()?.orders ?: emptyList()
                    val adapter = OrderAdapter(orders)
                    ordersRV.adapter = adapter
                } else {
                    Toast.makeText(requireContext(), "No orders found.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OrderedResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed to load orders: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}