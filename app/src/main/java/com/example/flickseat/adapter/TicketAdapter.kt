package com.example.flickseat.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blueiobase.api.android.strokedtextview.StrokedTextView
import com.example.flickseat.R
import com.example.flickseat.database.Ticket

class TicketAdapter(private val ticketList: List<Ticket>) :
    RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_ticket, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = ticketList[position]
        holder.bind(ticket)

        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = if (position == ticketList.size - 1) 500 else 30
        holder.itemView.layoutParams = layoutParams
    }

    override fun getItemCount(): Int = ticketList.size

    class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: StrokedTextView = itemView.findViewById(R.id.tvTitle)
        private val tvDay: StrokedTextView = itemView.findViewById(R.id.tvDay)
        private val tvTime: StrokedTextView = itemView.findViewById(R.id.tvTime)
        private val tvStatus: StrokedTextView = itemView.findViewById(R.id.tvStatus)
        private val tvSeatName: StrokedTextView = itemView.findViewById(R.id.tvSeatName)

        @SuppressLint("SetTextI18n")
        fun bind(ticket: Ticket) {
            tvTitle.text = ticket.movie_title
            tvDay.text = "Day: ${ticket.show_day}"
            tvTime.text = "Time: ${ticket.show_time}"
            tvStatus.text = "Status: ${ticket.status}"
            tvSeatName.text = "Seat ${ticket.seat_name}"
        }
    }
}
