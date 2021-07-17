package com.movies.core.searching

import com.movies.core.presentation.PresentationPort
import com.movies.core.entities.BusinessRule
import com.movies.core.entities.MoviesSection
import com.movies.core.pagination.PaginationPort
import io.reactivex.subjects.BehaviorSubject

interface SearchPort : PresentationPort, PaginationPort<MoviesSection> {
    val searchTerm: BehaviorSubject<String>
    val bindSearch: BusinessRule
}
