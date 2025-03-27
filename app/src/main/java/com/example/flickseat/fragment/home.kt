package com.example.flickseat.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.example.flickseat.R
import com.example.flickseat.adapter.NowShowingAdapter
import com.example.flickseat.adapter.ComingSoonAdapter
import com.example.flickseat.app_activity.Details
import com.example.flickseat.app_activity.SeatActivity
import com.example.flickseat.database.Movie
import com.example.flickseat.database.MovieResponse
import com.example.flickseat.database.RetrofitClient
import com.example.flickseat.tmdb_api.TMDBClient
import com.example.flickseat.tmdb_api.TMDBMovieResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class home : Fragment() {

    private val TAG = "homeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchNowShowingMovies(view)
        fetchComingSoonMovies(view)
    }

    private fun fetchNowShowingMovies(view: View) {
        val apiService = RetrofitClient.instance
        apiService.getNowShowing().enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val nowShowingResponse = response.body()
                    if (nowShowingResponse?.status == "success" && !nowShowingResponse.movies.isNullOrEmpty()) {
                        val movies = nowShowingResponse.movies
                        val randomIndex = Random(System.currentTimeMillis()).nextInt(movies.size)
                        val featuredMovie = movies[randomIndex]
                        displayFeaturedMovie(featuredMovie, view)

                        val remainingMovies = movies.toMutableList().apply { removeAt(randomIndex) }
                        val nowShowingRV = view.findViewById<RecyclerView>(R.id.nowShowingRV)
                        nowShowingRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        nowShowingRV.adapter = NowShowingAdapter(requireContext(), remainingMovies)
                    } else {
                        Log.e(TAG, "No now showing movies found: ${nowShowingResponse?.message}")
                    }
                } else {
                    Log.e(TAG, "Now showing server error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.e(TAG, "Now showing network error: ${t.message}", t)
            }
        })
    }

    private fun fetchComingSoonMovies(view: View) {
        val apiService = RetrofitClient.instance
        apiService.getComingSoon().enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val comingSoonResponse = response.body()
                    if (comingSoonResponse?.status == "success" && !comingSoonResponse.movies.isNullOrEmpty()) {
                        val comingSoonMovies = comingSoonResponse.movies
                        val comingSoonRV = view.findViewById<RecyclerView>(R.id.comingSoonRV)
                        comingSoonRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        comingSoonRV.adapter = ComingSoonAdapter(requireContext(), comingSoonMovies)
                    } else {
                        Log.e(TAG, "No coming soon movies found: ${comingSoonResponse?.message}")
                    }
                } else {
                    Log.e(TAG, "Coming soon server error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.e(TAG, "Coming soon network error: ${t.message}", t)
            }
        })
    }

    @SuppressLint("DiscouragedApi")
    private fun displayFeaturedMovie(movie: Movie, view: View) {
        val featuredImageView = view.findViewById<ImageView>(R.id.featured)
        val titleTextView = view.findViewById<TextView>(R.id.title)
        val genreTextView = view.findViewById<TextView>(R.id.genre)
        val bookSeatButton = view.findViewById<View>(R.id.btnBookSeat)
        val loading = view.findViewById<View>(R.id.loading) // Find ProgressBar

        titleTextView.text = movie.title
        genreTextView.text = movie.genre
        loading.visibility = View.VISIBLE // Show loading indicator

        TMDBClient.instance.getMovieDetails(movie.tmdb_id, "47478fd8ee52c5b99e942674d01e0c32")
            .enqueue(object : Callback<TMDBMovieResponse> {
                override fun onResponse(call: Call<TMDBMovieResponse>, response: Response<TMDBMovieResponse>) {
                    if (response.isSuccessful) {
                        val movieDetails = response.body()
                        val posterUrl = "https://image.tmdb.org/t/p/w500${movieDetails?.poster_path}"

                        // Load the poster using Glide
                        Glide.with(view.context)
                            .load(posterUrl)
                            .placeholder(R.color.background) // Show placeholder while loading
                            .error(R.color.background) // Fallback image if loading fails
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
                            .into(featuredImageView)
                    } else {
                        loading.visibility = View.GONE // Hide loading if API fails
                    }
                }

                override fun onFailure(call: Call<TMDBMovieResponse>, t: Throwable) {
                    loading.visibility = View.GONE // Hide loading on failure
                    featuredImageView.setImageResource(R.color.background) // Set fallback image
                }
            })

        featuredImageView.setOnClickListener {
            val intent = Intent(view.context, Details::class.java)
            intent.putExtra("tmdb_id", movie.tmdb_id)
            view.context.startActivity(intent)
        }

        bookSeatButton.setOnClickListener {
            val intent = Intent(view.context, SeatActivity::class.java)
            intent.putExtra("movie_id", movie.movie_id)
            view.context.startActivity(intent)
        }
    }
}
