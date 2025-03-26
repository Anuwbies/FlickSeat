package com.example.flickseat.adapter

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flickseat.R
import com.example.flickseat.database.Order
import com.google.android.material.textfield.TextInputEditText

class OrderAdapter(private val orders: List<Order>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private val groupedOrders: List<Order>

    init {
        val orderMap = mutableMapOf<String, Pair<Int, Order>>() // Store quantity & reference order

        for (order in orders.filter { it.status == "available" }) {
            val key = "${order.food_name.orEmpty()}_${order.drink_name.orEmpty()}"

            if (orderMap.containsKey(key)) {
                val existing = orderMap[key]!!
                orderMap[key] = existing.first + order.quantity to existing.second
            } else {
                orderMap[key] = order.quantity to order
            }
        }

        groupedOrders = orderMap.map { (_, pair) ->
            val (quantity, baseOrder) = pair
            baseOrder.copy(quantity = quantity) // Create a new Order with updated quantity
        }
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val etAmount: TextInputEditText = itemView.findViewById(R.id.etAmount)
        val btnMinus: ImageView = itemView.findViewById(R.id.btnMinus)
        val btnAdd: ImageView = itemView.findViewById(R.id.btnAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = groupedOrders[position]
        val maxQuantity = order.quantity // Maximum quantity available for this item

        val name = buildString {
            if (!order.food_name.isNullOrEmpty()) append(order.food_name)
            if (!order.drink_name.isNullOrEmpty()) {
                if (isNotEmpty()) append(", ")
                append(order.drink_name)
            }
        }

        holder.tvName.text = name.ifEmpty { "Unknown Item" }
        holder.tvQuantity.text = "x${maxQuantity}"

        // Initialize etAmount with 0
        holder.etAmount.setText("0")

        holder.btnAdd.setOnClickListener {
            val currentAmount = holder.etAmount.text.toString().toIntOrNull() ?: 0
            if (currentAmount < maxQuantity) { // Ensure it does not exceed available quantity
                holder.etAmount.setText((currentAmount + 1).toString())
            }
        }

        holder.btnMinus.setOnClickListener {
            val currentAmount = holder.etAmount.text.toString().toIntOrNull() ?: 0
            if (currentAmount > 0) { // Ensure it does not go below 0
                holder.etAmount.setText((currentAmount - 1).toString())
            }
        }

        // TextWatcher to limit manual input
        holder.etAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val enteredValue = s.toString().toIntOrNull() ?: 0
                if (enteredValue > maxQuantity) {
                    holder.etAmount.setText(maxQuantity.toString())
                    holder.etAmount.setSelection(holder.etAmount.text!!.length) // Move cursor to end
                } else if (enteredValue < 0) {
                    holder.etAmount.setText("0")
                    holder.etAmount.setSelection(1)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun getItemCount(): Int = groupedOrders.size
}