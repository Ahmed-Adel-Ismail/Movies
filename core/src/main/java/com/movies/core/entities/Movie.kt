package com.movies.core.entities

data class Movie(
    val title: String? = null,
    val year: String? = null,
    val cast: List<String>? = null,
    val genres: List<String>? = null,
    val rating: Int? = null
)
