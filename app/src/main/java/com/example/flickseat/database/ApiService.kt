package com.example.flickseat.database

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

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
}