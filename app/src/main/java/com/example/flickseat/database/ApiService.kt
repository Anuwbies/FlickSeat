package com.example.flickseat.database

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("signup.php")
    fun signUp(
        @Field("email") email: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("signin.php")
    fun signIn(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<ApiResponse>

    @POST("now_showing.php")
    fun getNowShowing(
    ): Call<MovieResponse>

    @POST("coming_soon.php")
    fun getComingSoon(
    ): Call<MovieResponse>

    @FormUrlEncoded
    @POST("get_movie_details.php")
    fun getMovieDetails(
        @Field("tmdb_id") tmdbId: Int
    ): Call<MovieResponse>

    // Fetch available showtimes for a specific movie
    @GET("showtimes.php")
    fun getShowtimes(
        @Query("movie_id") movieId: Int
    ): Call<ShowtimeResponse>

    // Fetch available seats for a specific showtime
    @GET("seats.php")
    fun getSeats(
        @Query("movie_id") movieId: Int,
        @Query("show_day") showDay: String,
        @Query("show_time") showTime: String
    ): Call<SeatResponse>

    // Book a seat
    @FormUrlEncoded
    @POST("book_seat.php")
    fun bookSeat(
        @Field("seat_id") seatId: Int
    ): Call<ApiResponse>
}