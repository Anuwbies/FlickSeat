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
    private val itemList: List<FoodDrink>,
    private val isDrinkList: Boolean // Identify if this is the drinks list
) : RecyclerView.Adapter<FoodDrinkAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPicture: ImageView = view.findViewById(R.id.ivPicture)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val btnMinus: ImageView = view.findViewById(R.id.btnMinus)
        val btnAdd: ImageView = view.findViewById(R.id.btnAdd)
        val etAmount: TextInputEditText = view.findViewById(R.id.etAmount)
        val tvTotalPrice: TextView = view.findViewById(R.id.tvTotalPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_fooddrink, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("DiscouragedApi", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]

        holder.tvName.text = item.name
        holder.tvPrice.text = "₱ ${item.price}"

        // Load image from drawable
        val resourceId = context.resources.getIdentifier(
            item.name.lowercase().replace(" ", "_"),
            "drawable",
            context.packageName
        )
        holder.ivPicture.setImageResource(if (resourceId != 0) resourceId else R.drawable.shonic)

        // ✅ Apply bottom margin of 100dp **only if it's the last drink**
        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        if (isDrinkList && position == itemList.size - 1) {
            layoutParams.bottomMargin = 200 // 100dp margin for the last drink item
        } else {
            layoutParams.bottomMargin = 0 // Reset margin for other items
        }
        holder.itemView.layoutParams = layoutParams

        // Quantity handling
        var quantity = 0
        holder.etAmount.setText(quantity.toString())

        holder.btnAdd.setOnClickListener {
            if (quantity < 99) {
                quantity++
                holder.etAmount.setText(quantity.toString())
                holder.tvTotalPrice.text = "₱ ${quantity * item.price}"
            }
        }

        holder.btnMinus.setOnClickListener {
            if (quantity > 0) {
                quantity--
                holder.etAmount.setText(quantity.toString())
                holder.tvTotalPrice.text = "₱ ${quantity * item.price}"
            }
        }

        // Listen for text input changes
        holder.etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                quantity = if (input.isNotEmpty()) {
                    val value = input.toInt()
                    when {
                        value > 99 -> 99
                        value < 0 -> 0
                        else -> value
                    }
                } else {
                    0
                }

                holder.etAmount.removeTextChangedListener(this)
                holder.etAmount.setText(quantity.toString())
                holder.etAmount.setSelection(holder.etAmount.text!!.length)
                holder.tvTotalPrice.text = "₱ ${quantity * item.price}"
                holder.etAmount.addTextChangedListener(this)
            }
        })
    }

    override fun getItemCount(): Int = itemList.size
}
