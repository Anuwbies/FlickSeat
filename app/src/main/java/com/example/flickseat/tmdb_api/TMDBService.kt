package com.example.flickseat.tmdb_api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBService {
    @GET("movie/{movie_id}/videos")
    fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): Call<TMDBVideoResponse>

    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): Call<TMDBMovieResponse>
}
