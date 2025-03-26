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
import com.example.flickseat.R
import com.example.flickseat.adapter.NowShowingAdapter
import com.example.flickseat.adapter.ComingSoonAdapter
import com.example.flickseat.app_activity.Details
import com.example.flickseat.app_activity.SeatActivity
import com.example.flickseat.database.Movie
import com.example.flickseat.database.MovieResponse
import com.example.flickseat.database.RetrofitClient
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

        titleTextView.text = movie.title
        genreTextView.text = movie.genre

        val context = view.context
        val resourceName = "p${movie.tmdb_id}"
        val resId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        if (resId != 0) {
            featuredImageView.setImageResource(resId)
        } else {
            featuredImageView.setImageResource(R.drawable.shonic)
        }

        featuredImageView.setOnClickListener {
            val intent = Intent(context, Details::class.java)
            intent.putExtra("tmdb_id", movie.tmdb_id)
            context.startActivity(intent)
        }

        bookSeatButton.setOnClickListener {
            val intent = Intent(context, SeatActivity::class.java)
            intent.putExtra("movie_id", movie.movie_id)
            context.startActivity(intent)
        }
    }
}
