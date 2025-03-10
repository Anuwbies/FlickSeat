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

    private val selectedSeats = mutableSetOf<Seat>()
    private val maxSelection = 5

    class SeatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSeat: TextView = view.findViewById(R.id.tvSeat)
        val container: ImageView = view.findViewById(R.id.container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_seat, parent, false)
        return SeatViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: SeatViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val seat = seats[position]
        val context = holder.itemView.context

        // Caching colors for optimization
        val greyColor = ContextCompat.getColor(context, R.color.greytext)
        val redColor = ContextCompat.getColor(context, R.color.red)
        val purpleColor = ContextCompat.getColor(context, R.color.purple)
        val lightGreyColor = ContextCompat.getColor(context, R.color.light_grey)
        val blackColor = ContextCompat.getColor(context, android.R.color.black)
        val whiteColor = ContextCompat.getColor(context, android.R.color.white)

        holder.tvSeat.text = seat.seat_name

        // Apply uniform spacing
        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(13, 13, 13, 13)
        holder.itemView.layoutParams = layoutParams

        if (seat.seat_id == -1) {
            // Placeholder seat (disabled)
            holder.container.imageTintList = ColorStateList.valueOf(lightGreyColor)
            holder.tvSeat.setTextColor(blackColor)
            holder.itemView.alpha = 0.5f
            holder.itemView.isClickable = false
            holder.itemView.isEnabled = false
        } else {
            // Actual seat
            holder.itemView.alpha = 1.0f
            holder.itemView.isClickable = true
            holder.itemView.isEnabled = true

            when {
                selectedSeats.contains(seat) -> {
                    // Selected seat
                    holder.container.imageTintList = ColorStateList.valueOf(purpleColor)
                    holder.tvSeat.setTextColor(whiteColor)
                }
                seat.status == "available" -> {
                    // Available seat
                    holder.container.imageTintList = ColorStateList.valueOf(greyColor)
                    holder.tvSeat.setTextColor(blackColor)
                }
                else -> {
                    // Booked seat
                    holder.container.imageTintList = ColorStateList.valueOf(redColor)
                    holder.tvSeat.setTextColor(whiteColor)
                }
            }

            holder.itemView.setOnClickListener {
                if (seat.status == "available") {
                    if (selectedSeats.contains(seat)) {
                        selectedSeats.remove(seat) // Deselect
                    } else {
                        if (selectedSeats.size < maxSelection) {
                            selectedSeats.add(seat)
                        } else {
                            Toast.makeText(context, "You can select up to 5 seats only.", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                    }
                    notifyDataSetChanged() // Ensures smooth UI updates
                    onSeatsSelected(selectedSeats.toList())
                }
            }
        }
    }

    override fun getItemCount(): Int = seats.size
}