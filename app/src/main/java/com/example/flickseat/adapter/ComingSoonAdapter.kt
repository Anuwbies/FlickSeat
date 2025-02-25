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

class ComingSoonAdapter(
    private val context: Context,
    private val movies: List<Movie>
) : RecyclerView.Adapter<ComingSoonAdapter.ComingSoonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComingSoonViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false)
        val itemWidthPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 100f, context.resources.displayMetrics
        ).toInt()
        val sideMarginPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 5f, context.resources.displayMetrics
        ).toInt()
        val bottomMarginPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 70f, context.resources.displayMetrics
        ).toInt()

        val layoutParams = view.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams.width = itemWidthPx
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            layoutParams.leftMargin = sideMarginPx
            layoutParams.rightMargin = sideMarginPx
            layoutParams.bottomMargin = bottomMarginPx
        }
        view.layoutParams = layoutParams

        return ComingSoonViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComingSoonViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    inner class ComingSoonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
            // On item click, pass the tmdb id to Details activity.
            itemView.setOnClickListener {
                val intent = Intent(context, Details::class.java)
                intent.putExtra("tmdb_id", movie.tmdb_id)
                context.startActivity(intent)
            }
        }
    }
}
