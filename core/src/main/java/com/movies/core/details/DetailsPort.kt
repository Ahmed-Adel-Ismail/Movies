package com.movies.core.details

import com.movies.core.entities.BusinessRule
import com.movies.core.pagination.PaginationPort
import com.movies.core.presentation.PresentationPort
import com.movies.core.searching.results.ThumbnailsPort
import io.reactivex.subjects.BehaviorSubject

interface DetailsPort : PresentationPort, ThumbnailsPort, PaginationPort<String> {
    val bindDetails: BusinessRule
}
