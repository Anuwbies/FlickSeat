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
import com.bumptech.glide.load.engine.GlideException
import com.example.flickseat.R
import com.example.flickseat.tmdb_api.Trailer

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
        fun bind(trailer: Trailer) {
            val trailerImage: ImageView = itemView.findViewById(R.id.trailer)
            val playIcon: ImageView = itemView.findViewById(R.id.playIcon)
            val loading: View = itemView.findViewById(R.id.loading)

            // Show loading spinner and hide play icon while loading
            loading.visibility = View.VISIBLE
            playIcon.visibility = View.GONE
            trailerImage.visibility = View.VISIBLE // Hide the image until loaded

            val thumbnailUrl = "https://img.youtube.com/vi/${trailer.youtubeId}/hqdefault.jpg"

            Glide.with(context)
                .load(thumbnailUrl)
                .placeholder(R.color.background) // Placeholder while loading
                .error(R.color.background) // Show error background if failed
                .listener(object : com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        loading.visibility = View.GONE // Hide loading indicator
                        trailerImage.visibility = View.VISIBLE // Show trailer image even if failed
                        playIcon.visibility = View.GONE // Keep play icon hidden
                        return false
                    }

                    override fun onResourceReady(
                        resource: android.graphics.drawable.Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        loading.visibility = View.GONE // Hide loading indicator
                        trailerImage.visibility = View.VISIBLE // Show loaded trailer image
                        playIcon.visibility = View.VISIBLE // Show play icon after loading
                        return false
                    }
                })
                .into(trailerImage)

            // Click listener to open YouTube trailer
            itemView.setOnClickListener {
                val youtubeAppUri = Uri.parse("vnd.youtube:${trailer.youtubeId}")
                val webUri = Uri.parse("https://www.youtube.com/watch?v=${trailer.youtubeId}")

                val intent = Intent(Intent.ACTION_VIEW, youtubeAppUri)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setPackage("com.google.android.youtube")

                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                }
            }
        }
    }
}