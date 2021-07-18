package com.movies.core.searching.results

import com.movies.core.entities.Movie
import com.movies.core.entities.PaginatedBatch
import com.movies.core.pagination.PaginationPort
import com.movies.core.presentation.PresentationPort
import io.reactivex.subjects.BehaviorSubject

interface ThumbnailsPort : PaginationPort<String> {
    val movie: BehaviorSubject<Movie>
}
