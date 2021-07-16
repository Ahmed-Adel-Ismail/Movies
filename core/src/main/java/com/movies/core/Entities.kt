package com.movies.core

@DslMarker
annotation class BusinessRules

typealias BusinessRule = Unit

data class Movie(
    val title: String? = null,
    val year: String? = null,
    val cast: List<String>? = null,
    val genres: List<String>? = null,
    val rating: Int? = null
)

data class MoviesSection(
    val year: String? = null,
    val movies: List<Movie>? = null
)

data class PaginatedBatch<T>(
    val key: String? = null,
    val pageNumber: Int? = null,
    val itemsPerPage: Int? = null,
    val items: List<T>? = null
)


