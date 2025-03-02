package com.example.flickseat.app_activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flickseat.R
import com.example.flickseat.adapter.Trailer
import com.example.flickseat.adapter.TrailerAdapter
import com.example.flickseat.database.Movie
import com.example.flickseat.database.MovieResponse
import com.example.flickseat.database.RetrofitClient
import com.example.flickseat.tmdb_api.TMDBClient
import com.example.flickseat.tmdb_api.TMDBVideoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Details : AppCompatActivity() {

    private val TAG = "DetailsActivity"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_details)

        val backButton = findViewById<ImageView>(R.id.back_btn)
        val btnBookSeat = findViewById<Button>(R.id.btnBookSeat)

        backButton.setOnClickListener {
            finish()
        }

        val tmdbId = intent.getIntExtra("tmdb_id", -1)

        if (tmdbId == -1) {
            Toast.makeText(this, "Invalid tmdb id", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        fetchMovieDetails(tmdbId, btnBookSeat)
        fetchMovieTrailers(tmdbId)
    }

    private fun fetchMovieDetails(tmdbId: Int, btnBookSeat: Button) {
        RetrofitClient.instance.getMovieDetails(tmdbId).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val movieResponse = response.body()
                    if (movieResponse?.status == "success" && !movieResponse.movies.isNullOrEmpty()) {
                        val movie = movieResponse.movies.first()
                        displayMovieDetails(movie)

                        // Pass movie_id
                        if (movie.status.equals("now showing", ignoreCase = true)) {
                            btnBookSeat.visibility = View.VISIBLE
                            btnBookSeat.isEnabled = true

                            btnBookSeat.setOnClickListener {
                                val intent = Intent(this@Details, SeatActivity::class.java)
                                intent.putExtra("movie_id", movie.movie_id)  // Pass movie_id
                                startActivity(intent)
                            }
                        } else {
                            btnBookSeat.visibility = View.GONE
                            btnBookSeat.isEnabled = false
                        }
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

    private fun fetchMovieTrailers(tmdbId: Int) {
        val apiKey = "47478fd8ee52c5b99e942674d01e0c32"
        TMDBClient.instance.getMovieVideos(tmdbId, apiKey).enqueue(object : Callback<TMDBVideoResponse> {
            override fun onResponse(call: Call<TMDBVideoResponse>, response: Response<TMDBVideoResponse>) {
                if (response.isSuccessful) {
                    val videoResponse = response.body()
                    if (videoResponse != null && videoResponse.results.isNotEmpty()) {
                        val trailers = videoResponse.results.filter {
                            it.site.equals("YouTube", ignoreCase = true) && it.type.equals("Trailer", ignoreCase = true)
                        }.map { Trailer(it.key, it.name) }

                        displayTrailers(trailers)
                    } else {
                        Log.e(TAG, "No trailers found")
                    }
                } else {
                    Log.e(TAG, "TMDB API error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<TMDBVideoResponse>, t: Throwable) {
                Log.e(TAG, "TMDB Network error: ${t.message}", t)
            }
        })
    }

    @SuppressLint("DiscouragedApi")
    private fun displayMovieDetails(movie: Movie) {
        val posterImageView = findViewById<ImageView>(R.id.moviePoster)
        val titleTextView = findViewById<TextView>(R.id.tvTitle)
        val genreTextView = findViewById<TextView>(R.id.tvGenre)
        val dateTextView = findViewById<TextView>(R.id.tvDate)
        val durationTextView = findViewById<TextView>(R.id.tvDuration)
        val ratingTextView = findViewById<TextView>(R.id.tvRating)
        val overviewTextView = findViewById<TextView>(R.id.tvOverview)

        titleTextView.text = movie.title
        genreTextView.text = movie.genre
        dateTextView.text = "${movie.release_date}  •"
        durationTextView.text = "  ${movie.duration}  •"
        ratingTextView.text = "  ☆ ${movie.rating}"
        overviewTextView.text = movie.overview

        val resourceName = "p${movie.tmdb_id}"
        val resId = resources.getIdentifier(resourceName, "drawable", packageName)
        if (resId != 0) {
            posterImageView.setImageResource(resId)
        } else {
            posterImageView.setImageResource(R.drawable.shonic)
        }

        titleTextView.maxLines = 1
        overviewTextView.maxLines = 3

        titleTextView.setOnClickListener {
            titleTextView.maxLines = if (titleTextView.maxLines == 1) Integer.MAX_VALUE else 1
            titleTextView.ellipsize = if (titleTextView.maxLines == 1) TextUtils.TruncateAt.END else null
        }

        overviewTextView.setOnClickListener {
            overviewTextView.maxLines = if (overviewTextView.maxLines == 3) Integer.MAX_VALUE else 3
            overviewTextView.ellipsize = if (overviewTextView.maxLines == 3) TextUtils.TruncateAt.END else null
        }
    }

    private fun displayTrailers(trailers: List<Trailer>) {
        val trailerRV = findViewById<RecyclerView>(R.id.trailerRV)
        trailerRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        trailerRV.adapter = TrailerAdapter(this, trailers)
    }
}
