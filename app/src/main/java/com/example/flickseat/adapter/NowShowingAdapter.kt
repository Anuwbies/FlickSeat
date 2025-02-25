package com.example.flickseat.adapter

import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flickseat.R
import com.example.flickseat.app_activity.Details
import com.example.flickseat.database.Movie

class NowShowingAdapter(
    private val context: Context,
    private val movies: List<Movie>
) : RecyclerView.Adapter<NowShowingAdapter.NowShowingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NowShowingViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false)
        // Convert 100dp and 5dp to pixels.
        val itemWidthPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 100f, context.resources.displayMetrics
        ).toInt()
        val marginPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 5f, context.resources.displayMetrics
        ).toInt()

        // Set the height to MATCH_PARENT (to fill RecyclerView height) and a fixed width.
        val layoutParams = view.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.width = itemWidthPx
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            layoutParams.leftMargin = marginPx
            layoutParams.rightMargin = marginPx
        }
        view.layoutParams = layoutParams

        return NowShowingViewHolder(view)
    }

    override fun onBindViewHolder(holder: NowShowingViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    inner class NowShowingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val moviePoster: ImageView = itemView.findViewById(R.id.moviePoster)
        private val movieTitle: TextView = itemView.findViewById(R.id.movieTitle)

        @Suppress("DiscouragedApi")
        fun bind(movie: Movie) {
            movieTitle.text = movie.title

            // Construct resource name: "p{tmdb_id}"
            val resourceName = "p${movie.tmdb_id}"
            val resId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
            if (resId != 0) {
                moviePoster.setImageResource(resId)
            } else {
                moviePoster.setImageResource(R.drawable.shonic)
            }

            // Set click listener to open Details activity and pass tmdb id.
            itemView.setOnClickListener {
                val intent = Intent(context, Details::class.java)
                intent.putExtra("tmdb_id", movie.tmdb_id)
                context.startActivity(intent)
            }
        }
    }
}
