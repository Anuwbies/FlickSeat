package com.example.flickseat.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flickseat.R
import com.example.flickseat.database.Order

class OrderAdapter(orders: List<Order>) :
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
        }.sortedWith(compareBy({ it.food_name.isNullOrEmpty() }, { it.food_name }, { it.drink_name }))
    }


    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val ivPicture: ImageView = itemView.findViewById(R.id.ivPicture)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "DiscouragedApi")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = groupedOrders[position]

        val name = buildString {
            if (!order.food_name.isNullOrEmpty()) append(order.food_name)
            if (!order.drink_name.isNullOrEmpty()) {
                if (isNotEmpty()) append(", ")
                append(order.drink_name)
            }
        }

        // Set ImageView based on food or drink name
        val context = holder.itemView.context
        val imageName = (order.food_name ?: order.drink_name)?.lowercase()?.replace(" ", "_") ?: "buttered_popcorn"
        val imageResId = context.resources.getIdentifier(imageName, "drawable", context.packageName)

        if (imageResId != 0) {
            holder.ivPicture.setImageResource(imageResId)
        } else {
            holder.ivPicture.setImageResource(R.drawable.buttered_popcorn) // Fallback image
        }

        holder.tvName.text = name.ifEmpty { "Unknown Item" }
        holder.tvQuantity.text = "x${order.quantity}"
    }

    override fun getItemCount(): Int = groupedOrders.size
}