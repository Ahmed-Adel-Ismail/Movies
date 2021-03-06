package com.movies.core.entities

data class MovieDetails(
    val movie: Movie? = null,
    val imagesUrls: PaginatedBatch<String>? = null
)
