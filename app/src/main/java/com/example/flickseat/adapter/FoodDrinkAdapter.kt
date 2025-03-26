package com.example.flickseat.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flickseat.R
import com.example.flickseat.database.FoodDrink
import com.google.android.material.textfield.TextInputEditText

class FoodDrinkAdapter(
    private val context: Context,
    val itemList: List<FoodDrink>,
    private val isFood: Boolean, // Add this flag to differentiate
    private val totalPriceChangeListener: OnTotalPriceChangeListener?
) : RecyclerView.Adapter<FoodDrinkAdapter.ViewHolder>() {

    interface OnTotalPriceChangeListener {
        fun onTotalPriceUpdated(foodTotalPrice: Int, drinkTotalPrice: Int)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPicture: ImageView = view.findViewById(R.id.ivPicture)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val btnMinus: ImageView = view.findViewById(R.id.btnMinus)
        val btnAdd: ImageView = view.findViewById(R.id.btnAdd)
        val etAmount: TextInputEditText = view.findViewById(R.id.etAmount)
        val tvTotalPrice: TextView = view.findViewById(R.id.tvTotalPrice)
    }

    private var totalOrderPrice = 0
    private val itemQuantities = mutableMapOf<Int, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_fooddrink, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("DiscouragedApi", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = itemList[position]
        holder.tvName.text = item.name
        holder.tvPrice.text = "₱ ${item.price}"

        val resourceId = context.resources.getIdentifier(
            item.name.lowercase().replace(" ", "_"), "drawable", context.packageName
        )
        holder.ivPicture.setImageResource(if (resourceId != 0) resourceId else R.drawable.shonic)

        var quantity = itemQuantities[position] ?: 0
        holder.etAmount.setText(quantity.toString())
        holder.tvTotalPrice.text = "₱ ${quantity * item.price}"

        fun updateTotalPrice() {
            val totalPrice = itemQuantities.entries.sumOf { (index, quantity) ->
                val item = itemList.getOrNull(index)
                (item?.price ?: 0) * quantity
            }

            // Update food or drink total based on isFood flag
            totalPriceChangeListener?.onTotalPriceUpdated(
                if (isFood) totalPrice else -1, // Keep food price, send -1 for drink if it's not updating
                if (!isFood) totalPrice else -1 // Keep drink price, send -1 for food if it's not updating
            )
        }

        holder.btnAdd.setOnClickListener {
            if (quantity < 99) {
                quantity++
                itemQuantities[position] = quantity
                holder.etAmount.setText(quantity.toString())
                holder.tvTotalPrice.text = "₱ ${quantity * item.price}"
                updateTotalPrice()
            }
        }

        holder.btnMinus.setOnClickListener {
            if (quantity > 0) {
                quantity--
                itemQuantities[position] = quantity
                holder.etAmount.setText(quantity.toString())
                holder.tvTotalPrice.text = "₱ ${quantity * item.price}"
                updateTotalPrice()
            }
        }

        holder.etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()

                // Prevent empty input
                if (input.isEmpty()) {
                    holder.etAmount.setText("0")
                    holder.etAmount.setSelection(1)
                    return
                }

                // Convert to number safely
                val value = input.toIntOrNull() ?: 0

                // Restrict between 0 and 99
                quantity = when {
                    value > 99 -> 99
                    value < 0 -> 0
                    else -> value
                }

                // Auto-correct input if it was above 99
                if (value != quantity) {
                    holder.etAmount.setText(quantity.toString())
                    holder.etAmount.setSelection(holder.etAmount.text?.length ?: 0) // Keep cursor at end
                }

                itemQuantities[position] = quantity
                holder.tvTotalPrice.text = "₱ ${quantity * item.price}"
                updateTotalPrice()
            }
        })
    }

    override fun getItemCount(): Int = itemList.size

    fun getItemQuantity(position: Int): Int {
        return itemQuantities[position] ?: 0
    }

    fun getTotalOrderPrice(): Int {
        return totalOrderPrice
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetOrder() {
        itemQuantities.clear()
        notifyDataSetChanged() // Refresh the RecyclerView
    }

}
