package com.example.flickseat.tmdb_api

data class TMDBVideoResponse(
    val id: Int,
    val results: List<TMDBVideo>
)

data class TMDBVideo(
    val id: String,
    val iso_639_1: String,
    val iso_3166_1: String,
    val key: String,
    val name: String,
    val site: String,
    val size: Int,
    val type: String
)

data class TMDBMovieResponse(
    val id: Int,
    val title: String,
    val tmdb_id: Int,
    val poster_path: String?
)

data class Trailer(
    val youtubeId: String,
    val name: String
)
