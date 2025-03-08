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

class TimeAdapter(
    private val times: List<String>,
    private val isPlaceholder: Boolean = false,
    private val onTimeSelected: (String) -> Unit
) : RecyclerView.Adapter<TimeAdapter.TimeViewHolder>() {

    private var selectedPosition = -1

    class TimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val container: ImageView = view.findViewById(R.id.container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_time, parent, false)

        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.marginStart = 8
        layoutParams.marginEnd = 8

        view.layoutParams = layoutParams
        return TimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val time = times[position]
        holder.tvTime.text = time

        val context = holder.itemView.context
        val selectedColor = ContextCompat.getColorStateList(context, R.color.purple)
        val defaultColor = ContextCompat.getColorStateList(context, R.color.greytext)
        val placeholderColor = ContextCompat.getColorStateList(context, R.color.light_grey)

        if (isPlaceholder) {
            holder.container.imageTintList = placeholderColor
            holder.tvTime.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            holder.itemView.isEnabled = false
            holder.itemView.alpha = 0.5f
        } else {
            holder.itemView.isEnabled = true
            holder.itemView.alpha = 1.0f

            if (position == selectedPosition) {
                holder.container.imageTintList = selectedColor
                holder.tvTime.setTextColor(ContextCompat.getColor(context, android.R.color.white))
            } else {
                holder.container.imageTintList = defaultColor
                holder.tvTime.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            }

            holder.itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = position

                if (previousPosition != -1) {
                    notifyItemChanged(previousPosition)
                }
                notifyItemChanged(selectedPosition)

                onTimeSelected(time)
            }
        }
    }

    override fun getItemCount(): Int = times.size
}
