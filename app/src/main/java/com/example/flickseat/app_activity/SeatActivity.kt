package com.example.flickseat.app_activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flickseat.R
import com.example.flickseat.adapter.SeatAdapter
import com.example.flickseat.adapters.DayAdapter
import com.example.flickseat.adapters.TimeAdapter
import com.example.flickseat.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SeatActivity : AppCompatActivity() {

    private lateinit var dayRecyclerView: RecyclerView
    private lateinit var timeRecyclerView: RecyclerView
    private lateinit var seatRecyclerView: RecyclerView
    private lateinit var priceTextView: TextView
    private lateinit var movieTitleTextView: TextView
    private lateinit var btnPrcdtoPayment: Button

    private var selectedDay: String = ""
    private var selectedTime: String = ""
    private var movieId: Int = 0
    private var moviePrice: Int = 0
    private lateinit var allShowtimes: List<Showtime>
    private var selectedSeats: List<Seat> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seat)

        val backButton = findViewById<ImageView>(R.id.back_btn)
        backButton.setOnClickListener {
            finish()
        }

        // Get movie ID from intent
        movieId = intent.getIntExtra("movie_id", 0)

        movieTitleTextView = findViewById(R.id.tvMovieTitle)
        priceTextView = findViewById(R.id.price)
        btnPrcdtoPayment = findViewById(R.id.btnPrcdtoPayment)

        // Initialize RecyclerViews
        dayRecyclerView = findViewById(R.id.dayRV)
        timeRecyclerView = findViewById(R.id.timeRV)
        seatRecyclerView = findViewById(R.id.seatRV)

        dayRecyclerView.layoutManager = GridLayoutManager(this, 5)
        timeRecyclerView.layoutManager = GridLayoutManager(this, 5)
        seatRecyclerView.layoutManager = GridLayoutManager(this, 6)

        fetchMovieDetails()
        showPlaceholderTimes()
        showPlaceholderSeats()
        fetchShowtimes()

        // Button click listener to log details
        btnPrcdtoPayment.setOnClickListener {
            logSelectedDetails()
        }
    }

    private fun fetchMovieDetails() {
        Log.d("SeatActivity", "Fetching movie details for movie_id: $movieId")

        RetrofitClient.instance.getMovieDetails(movieId = movieId).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val movie = response.body()?.movies?.firstOrNull()
                    if (movie != null) {
                        movieTitleTextView.text = movie.title
                        moviePrice = movie.movie_price
                        Log.d("SeatActivity", "Fetched movie: ${movie.title}, Price: ${movie.movie_price}")
                    } else {
                        showToast("Movie details not found")
                    }
                } else {
                    showToast("Failed to fetch movie details")
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.e("SeatActivity", "Error fetching movie details: ${t.message}")
                showToast("Failed to load movie details")
            }
        })
    }

    private fun fetchShowtimes() {
        Log.d("SeatActivity", "Fetching showtimes for movie_id: $movieId")

        RetrofitClient.instance.getShowtimes(movieId).enqueue(object : Callback<ShowtimeResponse> {
            override fun onResponse(call: Call<ShowtimeResponse>, response: Response<ShowtimeResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    allShowtimes = response.body()?.showtimes ?: emptyList()
                    val uniqueDays = allShowtimes.map { it.show_day }.distinct()

                    if (uniqueDays.isNotEmpty()) {
                        setupDayRecyclerView(uniqueDays)
                    } else {
                        showToast("No available showtimes")
                    }
                } else {
                    showToast("Failed to fetch showtimes")
                }
            }

            override fun onFailure(call: Call<ShowtimeResponse>, t: Throwable) {
                showToast("Failed to load showtimes")
            }
        })
    }

    private fun setupDayRecyclerView(days: List<String>) {
        dayRecyclerView.adapter = DayAdapter(days) { day ->
            if (selectedDay != day) {
                selectedDay = day
                timeRecyclerView.visibility = View.VISIBLE
                val timesForDay = allShowtimes.filter { it.show_day == day }.map { it.show_time }

                if (timesForDay.isNotEmpty()) {
                    setupTimeRecyclerView(timesForDay)
                } else {
                    showPlaceholderTimes()
                }

                selectedTime = ""
                showPlaceholderSeats()
            }
        }
    }

    private fun setupTimeRecyclerView(times: List<String>, isPlaceholder: Boolean = false) {
        timeRecyclerView.adapter = TimeAdapter(times, isPlaceholder) { time ->
            if (!isPlaceholder && selectedTime != time) {
                selectedTime = time
                fetchSeats()
            }
        }
    }

    private fun fetchSeats() {
        if (selectedDay.isEmpty() || selectedTime.isEmpty()) {
            showPlaceholderSeats()
            return
        }

        RetrofitClient.instance.getSeats(movieId, selectedDay, selectedTime).enqueue(object : Callback<SeatResponse> {
            override fun onResponse(call: Call<SeatResponse>, response: Response<SeatResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val seats = response.body()?.seats ?: emptyList()
                    setupSeatRecyclerView(seats)
                } else {
                    showToast("No available seats")
                    setupSeatRecyclerView(emptyList())
                }
            }

            override fun onFailure(call: Call<SeatResponse>, t: Throwable) {
                showToast("Failed to load seats")
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setupSeatRecyclerView(seats: List<Seat>) {
        seatRecyclerView.adapter = SeatAdapter(seats) { selectedSeats ->
            this.selectedSeats = selectedSeats
            val totalPrice = selectedSeats.size * moviePrice
            priceTextView.text = "₱ $totalPrice"
        }
    }

    private fun logSelectedDetails() {
        val selectedShowtime = allShowtimes.find { it.show_day == selectedDay && it.show_time == selectedTime }
        val showtimeId = selectedShowtime?.showtime_id ?: -1
        val selectedSeatIds = selectedSeats.map { it.seat_id }

        Log.d("SeatActivity", "Proceeding to payment with details:")
        Log.d("SeatActivity", "Movie ID: $movieId")
        Log.d("SeatActivity", "Showtime ID: $showtimeId")
        Log.d("SeatActivity", "Selected Seats: $selectedSeatIds")
        Log.d("SeatActivity", "Movie Price: ₱$moviePrice")

        if (showtimeId == -1 || selectedSeats.isEmpty()) {
            showToast("Please select a valid showtime and at least one seat.")
            return
        }

        // Proceed to the next step (e.g., navigate to the payment screen)
    }

    private fun showPlaceholderTimes() {
        val placeholderTimes = listOf("10:00am", "12:00pm", "4:00pm", "8:00pm", "12:00am")
        setupTimeRecyclerView(placeholderTimes, isPlaceholder = true)
    }

    private fun showPlaceholderSeats() {
        val rows = listOf("A", "B", "C", "D", "E")
        val cols = 6

        val placeholderSeats = mutableListOf<Seat>()
        for (row in rows) {
            for (col in 1..cols) {
                placeholderSeats.add(
                    Seat(seat_id = -1, seat_name = "$row$col", status = "available")
                )
            }
        }
        setupSeatRecyclerView(placeholderSeats)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}