package com.movies.core.entities

data class PaginatedBatch<T>(
    val key: String? = null,
    val pageNumber: Int? = null,
    val itemsPerPage: Int? = null,
    val items: List<T>? = null
)
