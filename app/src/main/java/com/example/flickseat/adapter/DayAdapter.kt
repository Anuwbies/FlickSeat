package com.example.flickseat.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.flickseat.R

class DayAdapter(
    private val days: List<String>,
    private val onDaySelected: (String) -> Unit
) : RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    private var selectedPosition = -1 // Track selected item

    class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDay: TextView = view.findViewById(R.id.tvDay)
        val container: ImageView = view.findViewById(R.id.container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)

        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.marginStart = 8 // Left margin
        layoutParams.marginEnd = 8   // Right margin

        view.layoutParams = layoutParams
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val day = days[position]
        holder.tvDay.text = day

        // Change background color when selected
        val context = holder.itemView.context
        val selectedColor = ContextCompat.getColorStateList(context, R.color.purple)
        val defaultColor = ContextCompat.getColorStateList(context, R.color.greytext)

        if (position == selectedPosition) {
            holder.container.imageTintList = selectedColor
            holder.tvDay.setTextColor(ContextCompat.getColor(context, android.R.color.white))
        } else {
            holder.container.imageTintList = defaultColor
            holder.tvDay.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        }

        // Handle click event
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position

            if (previousPosition != -1) {
                notifyItemChanged(previousPosition)
            }
            notifyItemChanged(selectedPosition)

            onDaySelected(day)
        }
    }

    override fun getItemCount(): Int = days.size
}