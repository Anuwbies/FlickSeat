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
    ): Call<UserResponse>

    @FormUrlEncoded
    @POST("signin.php")
    fun signIn(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<UserResponse>

    @FormUrlEncoded
    @POST("get_user_details.php")
    fun getUserDetails(
        @Field("user_id") userId: Int
    ): Call<UserResponse>


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

    @GET("showtimes.php")
    fun getShowtimes(
        @Query("movie_id") movieId: Int
    ): Call<ShowtimeResponse>

    @GET("seats.php")
    fun getSeats(
        @Query("movie_id") movieId: Int,
        @Query("show_day") showDay: String,
        @Query("show_time") showTime: String
    ): Call<SeatResponse>

    @FormUrlEncoded
    @POST("book_seat.php")
    fun bookSeat(
        @Field("seat_id") seatId: Int
    ): Call<UserResponse>
}