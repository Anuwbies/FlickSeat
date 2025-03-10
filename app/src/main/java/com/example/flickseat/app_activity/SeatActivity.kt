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

    private var selectedDay: String = ""
    private var selectedTime: String = ""
    private var movieId: Int = 0
    private var moviePrice: Int = 0
    private lateinit var allShowtimes: List<Showtime>

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
        moviePrice = intent.getIntExtra("movie_price", 0)
        val movieTitle = intent.getStringExtra("movie_title") ?: "Unknown Movie" // Default value if null

        Log.d("SeatActivity", "Received movie_id: $movieId, movie_title: $movieTitle")

        findViewById<TextView>(R.id.tvMovieTitle).text = movieTitle
        priceTextView = findViewById(R.id.price)

        // Initialize RecyclerViews
        dayRecyclerView = findViewById(R.id.dayRV)
        timeRecyclerView = findViewById(R.id.timeRV)
        seatRecyclerView = findViewById(R.id.seatRV)

        dayRecyclerView.layoutManager = GridLayoutManager(this, 5)
        timeRecyclerView.layoutManager = GridLayoutManager(this, 5)
        seatRecyclerView.layoutManager = GridLayoutManager(this, 6)

        showPlaceholderTimes()
        showPlaceholderSeats()

        fetchShowtimes()
    }

    private fun fetchShowtimes() {
        Log.d("SeatActivity", "Fetching showtimes for movie_id: $movieId")

        RetrofitClient.instance.getShowtimes(movieId).enqueue(object : Callback<ShowtimeResponse> {
            override fun onResponse(call: Call<ShowtimeResponse>, response: Response<ShowtimeResponse>) {
                Log.d("SeatActivity", "Showtime API Response Code: ${response.code()}")

                if (response.isSuccessful && response.body()?.status == "success") {
                    allShowtimes = response.body()?.showtimes ?: emptyList()
                    val uniqueDays = allShowtimes.map { it.show_day }.distinct()

                    if (uniqueDays.isNotEmpty()) {
                        setupDayRecyclerView(uniqueDays)
                    } else {
                        Log.e("SeatActivity", "No available showtimes received")
                        showToast("No available showtimes")
                    }
                } else {
                    Log.e("SeatActivity", "Error: No successful response")
                    showToast("No available showtimes")
                }
            }

            override fun onFailure(call: Call<ShowtimeResponse>, t: Throwable) {
                Log.e("SeatActivity", "Error fetching showtimes: ${t.message}")
                showToast("Failed to load showtimes")
            }
        })
    }

    private fun setupDayRecyclerView(days: List<String>) {
        Log.d("SeatActivity", "Setting up day recycler with days: $days")

        dayRecyclerView.adapter = DayAdapter(days) { day ->
            if (selectedDay != day) {
                selectedDay = day
                Log.d("SeatActivity", "Selected Day: $selectedDay")

                timeRecyclerView.visibility = View.VISIBLE
                val timesForDay = allShowtimes.filter { it.show_day == day }.map { it.show_time }

                if (timesForDay.isNotEmpty()) {
                    setupTimeRecyclerView(timesForDay)
                } else {
                    showPlaceholderTimes()
                }

                selectedTime = ""
                showPlaceholderSeats()
            } else {
                Log.d("SeatActivity", "Same day selected, keeping time selection.")
            }
        }
    }

    private fun setupTimeRecyclerView(times: List<String>, isPlaceholder: Boolean = false) {
        Log.d("SeatActivity", "Setting up time recycler with times: $times (Placeholder: $isPlaceholder)")

        timeRecyclerView.adapter = TimeAdapter(times, isPlaceholder) { time ->
            if (!isPlaceholder && selectedTime != time) {
                selectedTime = time
                Log.d("SeatActivity", "Selected Time: $selectedTime")
                fetchSeats()
            }
        }
    }

    private fun fetchSeats() {
        Log.d("SeatActivity", "Fetching seats for movie_id: $movieId, day: '$selectedDay', time: '$selectedTime'")

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
        Log.d("SeatActivity", "Setting up seat recycler with seats: $seats")

        seatRecyclerView.adapter = SeatAdapter(seats) { selectedSeats ->
            Log.d("SeatActivity", "Selected seats: $selectedSeats")

            // Calculate total price
            val totalPrice = selectedSeats.size * moviePrice
            priceTextView.text = "₱ $totalPrice"
        }
    }

    private fun showPlaceholderTimes() {
        Log.d("SeatActivity", "Displaying placeholder showtimes")

        val placeholderTimes = listOf("10:00am", "12:00pm", "4:00pm", "8:00pm", "12:00am")

        setupTimeRecyclerView(placeholderTimes, isPlaceholder = true)
    }

    private fun showPlaceholderSeats() {
        Log.d("SeatActivity", "Displaying placeholder seats")

        val rows = listOf("A", "B", "C", "D", "E")
        val cols = 6

        val placeholderSeats = mutableListOf<Seat>()

        for (row in rows) {
            for (col in 1..cols) {
                val seatName = "$row$col"
                placeholderSeats.add(
                    Seat(
                        seat_id = -1,
                        seat_name = seatName,
                        status = "available"
                    )
                )
            }
        }
        setupSeatRecyclerView(placeholderSeats)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}