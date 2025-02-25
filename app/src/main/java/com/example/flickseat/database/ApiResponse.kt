package com.example.flickseat.database

data class ApiResponse(
    val status: String,
    val message: String,
    val user: User? = null
)

data class User(
    val id: Int,
    val email: String,
    val username: String
)

data class Movie(
    val id: Int,
    val title: String,
    val genre: String,
    val release_date: String,
    val duration: String,
    val overview: String,
    val rating: Double,
    val tmdb_id: Int,
    val status: String
)

data class MovieResponse(
    val status: String,
    val movies: List<Movie>?,
    val message: String? = null
)