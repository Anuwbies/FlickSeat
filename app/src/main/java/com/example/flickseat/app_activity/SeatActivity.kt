package com.example.flickseat.app_activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.google.android.material.bottomsheet.BottomSheetDialog
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

        movieId = intent.getIntExtra("movie_id", 0)

        movieTitleTextView = findViewById(R.id.tvMovieTitle)
        priceTextView = findViewById(R.id.price)
        btnPrcdtoPayment = findViewById(R.id.btnPrcdtoPayment)

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

        btnPrcdtoPayment.setOnClickListener {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Please select a seat to proceed.", Toast.LENGTH_SHORT).show()
            } else {
                showPaymentBottomSheet()
            }
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
                selectedSeats = emptyList()
                priceTextView.text = "₱ 0"

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
                selectedSeats = emptyList()
                priceTextView.text = "₱ 0"
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
            priceTextView.text = "₱ ${selectedSeats.size * moviePrice}"
        }
    }

    private var bottomSheetDialog: BottomSheetDialog? = null
    private var selectedPaymentMethod: Button? = null

    @SuppressLint("SetTextI18n")
    private fun showPaymentBottomSheet() {
        if (bottomSheetDialog != null && bottomSheetDialog!!.isShowing) {
            return
        }

        val dimBg =findViewById<ImageView>(R.id.dimBg) // Find dimBg ImageView

        bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_payment, null)
        bottomSheetDialog!!.setContentView(view)

        val btnCard = view.findViewById<Button>(R.id.btnCard)
        val btnBank = view.findViewById<Button>(R.id.btnBank)
        val btnGpay = view.findViewById<Button>(R.id.btnGpay)
        val btnGcash = view.findViewById<Button>(R.id.btnGcash)
        val priceTextView = view.findViewById<TextView>(R.id.priceinpayment)
        val btnMakePayment = view.findViewById<Button>(R.id.btnMakePayment)

        val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)
        val selectedShowtime = allShowtimes.find { it.show_day == selectedDay && it.show_time == selectedTime }
        val showtimeId = selectedShowtime?.showtime_id ?: -1
        val selectedSeatIds = selectedSeats.map { it.seat_id }

        val totalPrice = selectedSeats.size * moviePrice
        priceTextView.text = "₱ $totalPrice"

        val paymentButtons = listOf(btnCard, btnBank, btnGpay, btnGcash)

        fun getDrawableFromStart(button: Button): Int {
            return when (button.id) {
                R.id.btnCard -> R.drawable.credit_card
                R.id.btnBank -> R.drawable.online_bank
                R.id.btnGpay -> R.drawable.g_pay
                R.id.btnGcash -> R.drawable.gcash
                else -> 0
            }
        }

        fun selectPaymentButton(selectedButton: Button) {
            selectedPaymentMethod = selectedButton
            for (button in paymentButtons) {
                button.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStart(button), 0, R.drawable.unchecked_radio, 0)
            }
            selectedButton.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStart(selectedButton), 0, R.drawable.checked_radio, 0)
        }

        selectedPaymentMethod?.let { previouslySelected ->
            selectPaymentButton(
                paymentButtons.firstOrNull { it.id == previouslySelected.id } ?: return@let
            )
        }

        btnCard.setOnClickListener { selectPaymentButton(btnCard) }
        btnBank.setOnClickListener { selectPaymentButton(btnBank) }
        btnGpay.setOnClickListener { selectPaymentButton(btnGpay) }
        btnGcash.setOnClickListener { selectPaymentButton(btnGcash) }

        btnMakePayment.setOnClickListener {
            if (userId == -1 || showtimeId == -1 || selectedSeats.isEmpty()) {
                Toast.makeText(this, "Invalid transaction details.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedPaymentMethod == null) {
                Toast.makeText(this, "Please choose a payment method.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Booking seat/s, Please Wait...", Toast.LENGTH_SHORT).show() // Notify user once

            val totalSeats = selectedSeats.size
            var bookedSeatsCount = 0
            val delayBetweenBookings = 500L // 500 milliseconds delay

            selectedSeats.forEachIndexed { index, seat ->
                val delayTime = index * delayBetweenBookings

                Handler(Looper.getMainLooper()).postDelayed({
                    if (seat.seat_id == -1) {
                        Log.e("TicketBooking", "Skipping invalid seat: ${seat.seat_name}")
                        return@postDelayed
                    }

                    Log.d("TicketBooking", "Booking seat: ID=${seat.seat_id}, Name=${seat.seat_name}")

                    RetrofitClient.instance.insertTicket(
                        userId = userId,
                        movieId = movieId,
                        showtimeId = showtimeId,
                        seatId = seat.seat_id,
                        ticketPrice = moviePrice
                    ).enqueue(object : Callback<TicketResponse> {
                        override fun onResponse(call: Call<TicketResponse>, response: Response<TicketResponse>) {
                            if (response.isSuccessful) {
                                val ticketResponse = response.body()
                                if (ticketResponse?.status == "success") {
                                    bookSeat(seat.seat_id)
                                    Log.d("TicketBooking", "Successfully booked seat: ${seat.seat_id}")
                                    bookedSeatsCount++
                                } else {
                                    Log.e("TicketBooking", "Server error: ${ticketResponse?.message}")
                                    Toast.makeText(this@SeatActivity, "Failed to book seat ${seat.seat_name}: ${ticketResponse?.message}", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val errorResponse = response.errorBody()?.string()
                                Log.e("TicketBooking", "Failed to book seat ${seat.seat_id}. Response: $errorResponse")
                                Toast.makeText(this@SeatActivity, "Failed to book seat ${seat.seat_name}. Server error.", Toast.LENGTH_SHORT).show()
                            }

                            if (bookedSeatsCount == totalSeats) {
                                Toast.makeText(this@SeatActivity, "Seat/s booked successfully", Toast.LENGTH_SHORT).show()

                                bottomSheetDialog?.dismiss()
                                dimBg.visibility = View.GONE // Hide dim background
                            }
                        }

                        override fun onFailure(call: Call<TicketResponse>, t: Throwable) {
                            Log.e("TicketBooking", "Network error: ${t.message}")
                            Toast.makeText(this@SeatActivity, "Failed to book seat ${seat.seat_name}. Network error.", Toast.LENGTH_SHORT).show()
                        }
                    })
                }, delayTime)
            }

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this@SeatActivity, Botnav::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }, totalSeats * delayBetweenBookings)

        }

        bottomSheetDialog?.setOnDismissListener {
            bottomSheetDialog = null
            dimBg.visibility = View.GONE // Show dim background
        }

        bottomSheetDialog?.show()

        dimBg.postDelayed({
            dimBg.visibility = View.VISIBLE
        }, 100) // Delay of 200 milliseconds

    }

    private fun bookSeat(seatId: Int) {
        RetrofitClient.instance.bookSeat(seatId).enqueue(object : Callback<BookSeatResponse> {
            override fun onResponse(call: Call<BookSeatResponse>, response: Response<BookSeatResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("SeatBooking", "Response: $body")

                    if (body?.status == "success") {
                        Log.d("SeatBooking", "Seat $seatId marked as taken.")
                    } else {
                        Log.e("SeatBooking", "Failed to update seat $seatId status: ${body?.message}")
                    }
                } else {
                    Log.e("SeatBooking", "Unexpected response for seat $seatId: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<BookSeatResponse>, t: Throwable) {
                Log.e("SeatBooking", "Network error while updating seat $seatId status: ${t.message}")
            }
        })
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