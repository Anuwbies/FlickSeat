package com.example.flickseat.tmdb_api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TMDBClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    val instance: TMDBService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TMDBService::class.java)
    }
}