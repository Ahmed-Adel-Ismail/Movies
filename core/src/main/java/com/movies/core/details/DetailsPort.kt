package com.movies.core.details

import com.movies.core.entities.BusinessRule
import com.movies.core.pagination.PaginationPort
import com.movies.core.presentation.PresentationPort
import io.reactivex.subjects.BehaviorSubject

interface DetailsPort : PresentationPort, PaginationPort<String> {
    val title: BehaviorSubject<String>
    val year: BehaviorSubject<String>
    val genres: BehaviorSubject<List<String>>
    val cast: BehaviorSubject<List<String>>
    val bindDetails: BusinessRule
}
