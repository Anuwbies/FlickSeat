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
    val movie_price: Int,
    var poster_path: String
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

data class Ticket(
    val ticket_id: Int,
    val purchase_date: String,
    val ticket_price: Int,
    val status: String,
    val movie_title: String,
    val show_day: String,
    val show_time: String,
    val seat_name: String
)

data class UserTicketResponse(
    val status: String,
    val tickets: List<Ticket>?
)

data class FoodDrink(
    val id: Int,
    val name: String,
    val price: Int
)

data class FoodDrinkResponse(
    val status: String,
    val foods: List<FoodDrink>?,
    val drinks: List<FoodDrink>?,
    val message: String? = null
)

data class OrderResponse(
    val status: String,
    val message: String
)

data class Order(
    val order_id: Int,
    val user_id: Int,
    val food_id: Int?,
    val food_name: String?,
    val drink_id: Int?,
    val drink_name: String?,
    val quantity: Int,
    val status: String
)

data class OrderedResponse(
    val status: String,
    val orders: List<Order>?,
    val message: String? = null
)
