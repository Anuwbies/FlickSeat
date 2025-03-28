package com.example.flickseat.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.flickseat.R

class StatusAdapter(
    private val statusList: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<StatusAdapter.StatusViewHolder>() {

    private var selectedStatus: String = "All"  // Default selected status

    inner class StatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnStatus: AppCompatButton = itemView.findViewById(R.id.btnStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_status, parent, false)
        return StatusViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val status = statusList[position]
        holder.btnStatus.text = status

        // Change background based on selection
        val background = if (status == selectedStatus) R.drawable.btn_purple else R.drawable.btn_empty
        holder.btnStatus.background = ContextCompat.getDrawable(holder.itemView.context, background)

        holder.btnStatus.setOnClickListener {
            selectedStatus = status
            onClick(status)
            notifyDataSetChanged()  // Refresh UI to update button states
        }
    }

    override fun getItemCount(): Int = statusList.size
}
