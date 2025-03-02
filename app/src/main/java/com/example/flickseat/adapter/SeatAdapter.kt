package com.example.flickseat.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.flickseat.R
import com.example.flickseat.database.Seat

class SeatAdapter(
    private val seats: List<Seat>,
    private val onSeatsSelected: (List<Seat>) -> Unit
) : RecyclerView.Adapter<SeatAdapter.SeatViewHolder>() {

    private val selectedSeats = mutableSetOf<Seat>() // Track selected seats
    private val maxSelection = 5 // Maximum selectable seats

    class SeatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSeat: TextView = view.findViewById(R.id.tvSeat)
        val container: ImageView = view.findViewById(R.id.container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_seat, parent, false)
        return SeatViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeatViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val seat = seats[position]
        val context = holder.itemView.context

        holder.tvSeat.text = seat.seat_name

        // Apply uniform spacing
        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(13, 13, 13, 13)
        holder.itemView.layoutParams = layoutParams

        if (seat.seat_id == -1) { // Placeholder seat
            holder.container.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.light_grey)
            )
            holder.tvSeat.setTextColor(ContextCompat.getColor(context, R.color.black))
            holder.itemView.alpha = 0.5f // Dim placeholder seat
            holder.itemView.isClickable = false
            holder.itemView.isEnabled = false
        } else { // Actual seat
            holder.itemView.alpha = 1.0f
            holder.itemView.isClickable = true
            holder.itemView.isEnabled = true

            if (seat.status == "available") {
                holder.container.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.greytext)
                )
                holder.tvSeat.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            } else {
                holder.container.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.red)
                )
                holder.tvSeat.setTextColor(ContextCompat.getColor(context, android.R.color.white))
            }

            // Apply selection effect
            if (selectedSeats.contains(seat)) {
                holder.container.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.purple)
                )
                holder.tvSeat.setTextColor(ContextCompat.getColor(context, android.R.color.white))
            }

            // Handle click event for selectable seats
            holder.itemView.setOnClickListener {
                if (seat.status == "available") {
                    if (selectedSeats.contains(seat)) {
                        selectedSeats.remove(seat) // Deselect
                    } else {
                        if (selectedSeats.size < maxSelection) {
                            selectedSeats.add(seat) // Select if limit not reached
                        } else {
                            Toast.makeText(context, "You can select up to 5 seats only.", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener // Ignore click if limit reached
                        }
                    }
                    notifyItemChanged(position) // Update UI for this item
                    onSeatsSelected(selectedSeats.toList()) // Notify selection change
                }
            }
        }
    }

    override fun getItemCount(): Int = seats.size
}