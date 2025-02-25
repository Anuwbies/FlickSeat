package com.example.flickseat.app_activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.flickseat.R
import com.example.flickseat.database.Movie
import com.example.flickseat.database.MovieResponse
import com.example.flickseat.database.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Details : AppCompatActivity() {

    private val TAG = "DetailsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_details)

        // Get tmdb_id from intent extras.
        val tmdbId = intent.getIntExtra("tmdb_id", -1)
        if (tmdbId == -1) {
            Toast.makeText(this, "Invalid movie id", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        fetchMovieDetails(tmdbId)
    }

    private fun fetchMovieDetails(tmdbId: Int) {
        RetrofitClient.instance.getMovieDetails(tmdbId).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val movieResponse = response.body()
                    if (movieResponse?.status == "success" && !movieResponse.movies.isNullOrEmpty()) {
                        // Expecting one movie.
                        val movie = movieResponse.movies.first()
                        displayMovieDetails(movie)
                    } else {
                        Toast.makeText(this@Details, "Movie details not found", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.e(TAG, "Server error: ${response.errorBody()?.string()}")
                    Toast.makeText(this@Details, "Server error", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.e(TAG, "Network error: ${t.message}", t)
                Toast.makeText(this@Details, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    @SuppressLint("DiscouragedApi")
    private fun displayMovieDetails(movie: Movie) {
        // Bind views from your layout.
        val posterImageView = findViewById<ImageView>(R.id.moviePoster)
        val titleTextView = findViewById<TextView>(R.id.tvTitle)
        val genreTextView = findViewById<TextView>(R.id.tvGenre)
        val dateTextView = findViewById<TextView>(R.id.tvDate)
        val durationTextView = findViewById<TextView>(R.id.tvDuration)
        val ratingTextView = findViewById<TextView>(R.id.tvRating)
        val overviewTextView = findViewById<TextView>(R.id.tvOverview)

        // Set movie details.
        titleTextView.text = movie.title
        genreTextView.text = movie.genre
        dateTextView.text = "${movie.release_date}  •"
        durationTextView.text = "  ${movie.duration}  •"
        ratingTextView.text = "  ☆ ${movie.rating}"
        overviewTextView.text = movie.overview

        // Load poster from drawable resource named "p{tmdb_id}".
        val resourceName = "p${movie.tmdb_id}"
        val resId = resources.getIdentifier(resourceName, "drawable", packageName)
        if (resId != 0) {
            posterImageView.setImageResource(resId)
        } else {
            posterImageView.setImageResource(R.drawable.p939243)
        }

        // Set up click listeners to toggle expansion of title and overview.
        // Initially, title and overview are set to a single line.
        titleTextView.maxLines = 1
        overviewTextView.maxLines = 3

        titleTextView.setOnClickListener {
            // Toggle between one line and unlimited lines.
            if (titleTextView.maxLines == 1) {
                titleTextView.maxLines = Integer.MAX_VALUE
                titleTextView.ellipsize = null
            } else {
                titleTextView.maxLines = 1
                titleTextView.ellipsize = android.text.TextUtils.TruncateAt.END
            }
        }

        overviewTextView.setOnClickListener {
            // Toggle between 3 lines and unlimited lines.
            if (overviewTextView.maxLines == 3) {
                overviewTextView.maxLines = Integer.MAX_VALUE
                overviewTextView.ellipsize = null
            } else {
                overviewTextView.maxLines = 3
                overviewTextView.ellipsize = android.text.TextUtils.TruncateAt.END
            }
        }
    }
}