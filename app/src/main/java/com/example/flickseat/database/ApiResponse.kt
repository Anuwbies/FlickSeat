package com.example.flickseat.database

data class UserResponse(
    val status: String,
    val message: String,
    val user: User? = null
)

data class User(
    val user_id: Int,
    val email: String,
    val username: String
)

data class Movie(
    val movie_id: Int,
    val title: String,
    val genre: String,
    val release_date: String,
    val duration: String,
    val overview: String,
    val rating: Double,
    val tmdb_id: Int,
    val status: String,
    val movie_price: Int
)

data class MovieResponse(
    val status: String,
    val movies: List<Movie>?,
    val message: String? = null
)

data class Showtime(
    val showtime_id: Int,
    val show_day: String,
    val show_time: String
)

data class ShowtimeResponse(
    val status: String,
    val showtimes: List<Showtime>?
)

data class Seat(
    val seat_id: Int,
    val seat_name: String,
    val status: String
)

data class SeatResponse(
    val status: String,
    val seats: List<Seat>?
)

data class TicketResponse(
    val status: String,
    val message: String
)

data class BookSeatResponse(
    val status: String,
    val message: String
)