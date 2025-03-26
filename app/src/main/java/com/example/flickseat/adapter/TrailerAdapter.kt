package com.example.flickseat.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flickseat.R

data class Trailer(
    val youtubeId: String,
    val name: String
)

class TrailerAdapter(
    private val context: Context,
    private val trailers: List<Trailer>
) : RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_trailer, parent, false)
        return TrailerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrailerViewHolder, position: Int) {
        val sideMarginPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 5f, context.resources.displayMetrics
        ).toInt()
        val startMarginPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f, context.resources.displayMetrics
        ).toInt()
        val endMarginPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f, context.resources.displayMetrics
        ).toInt()
        val bottomMarginPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 90f, context.resources.displayMetrics
        ).toInt()

        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams

        when (position) {
            0 -> {
                layoutParams.leftMargin = startMarginPx
                layoutParams.rightMargin = sideMarginPx
            }
            itemCount - 1 -> {
                layoutParams.leftMargin = sideMarginPx
                layoutParams.rightMargin = endMarginPx
            }
            else -> {
                layoutParams.leftMargin = sideMarginPx
                layoutParams.rightMargin = sideMarginPx
            }
        }
        layoutParams.bottomMargin = bottomMarginPx
        holder.itemView.layoutParams = layoutParams

        holder.bind(trailers[position])
    }

    override fun getItemCount(): Int = trailers.size

    inner class TrailerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trailerImage: ImageView = itemView.findViewById(R.id.trailer)

        fun bind(trailer: Trailer) {
            trailerImage.setImageResource(R.color.greytext)

            val thumbnailUrl = "https://img.youtube.com/vi/${trailer.youtubeId}/hqdefault.jpg"
            Glide.with(context)
                .load(thumbnailUrl)
                .placeholder(R.color.greytext)
                .into(trailerImage)

            itemView.setOnClickListener {
                val youtubeUrl = "https://www.youtube.com/watch?v=${trailer.youtubeId}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl))
                context.startActivity(intent)
            }
        }
    }
}
