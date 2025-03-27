package com.example.flickseat.adapter

import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.example.flickseat.R
import com.example.flickseat.app_activity.Details
import com.example.flickseat.database.Movie
import com.example.flickseat.tmdb_api.TMDBClient
import com.example.flickseat.tmdb_api.TMDBMovieResponse
import retrofit2.Call
import retrofit2.Response

class NowShowingAdapter(
    private val context: Context,
    private val movies: List<Movie>
) : RecyclerView.Adapter<NowShowingAdapter.NowShowingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NowShowingViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false)
        val itemWidthPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 100f, context.resources.displayMetrics
        ).toInt()
        val marginPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 5f, context.resources.displayMetrics
        ).toInt()

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
        private val loading: View = itemView.findViewById(R.id.loading) // Find ProgressBar

        @Suppress("DiscouragedApi")
        fun bind(movie: Movie) {
            movieTitle.text = movie.title
            loading.visibility = View.VISIBLE // Show loading indicator

            // Fetch movie poster from TMDB API
            TMDBClient.instance.getMovieDetails(movie.tmdb_id, "47478fd8ee52c5b99e942674d01e0c32")
                .enqueue(object : retrofit2.Callback<TMDBMovieResponse> {
                    override fun onResponse(call: Call<TMDBMovieResponse>, response: Response<TMDBMovieResponse>) {
                        if (response.isSuccessful) {
                            val movieDetails = response.body()
                            val posterUrl = "https://image.tmdb.org/t/p/w500${movieDetails?.poster_path}"

                            // Load image with Glide
                            Glide.with(context)
                                .load(posterUrl)
                                .placeholder(R.color.background) // Placeholder image
                                .error(R.color.background) // Error image
                                .listener(object : com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        loading.visibility = View.GONE // Hide loading if image fails
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: android.graphics.drawable.Drawable?,
                                        model: Any?,
                                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                                        dataSource: com.bumptech.glide.load.DataSource?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        loading.visibility = View.GONE // Hide loading when image loads
                                        return false
                                    }
                                })
                                .into(moviePoster)
                        } else {
                            loading.visibility = View.GONE // Hide loading if API fails
                        }
                    }

                    override fun onFailure(call: Call<TMDBMovieResponse>, t: Throwable) {
                        loading.visibility = View.GONE // Hide loading on failure
                        moviePoster.setImageResource(R.color.background) // Set fallback image
                    }
                })

            itemView.setOnClickListener {
                val intent = Intent(context, Details::class.java)
                intent.putExtra("tmdb_id", movie.tmdb_id)
                context.startActivity(intent)
            }
        }
    }
}
