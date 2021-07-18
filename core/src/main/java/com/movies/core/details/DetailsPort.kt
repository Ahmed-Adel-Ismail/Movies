package com.movies.core.details

import com.movies.core.entities.BusinessRule
import com.movies.core.entities.Movie
import com.movies.core.pagination.PaginationPort
import com.movies.core.presentation.PresentationPort
import io.reactivex.subjects.BehaviorSubject

interface DetailsPort : PresentationPort, PaginationPort<String> {
    val movie: BehaviorSubject<Movie>
    val bindDetails: BusinessRule
}
