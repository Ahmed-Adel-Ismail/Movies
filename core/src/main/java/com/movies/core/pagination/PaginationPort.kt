package com.movies.core.pagination

import com.movies.core.entities.PaginatedBatch
import com.movies.core.presentation.PresentationPort
import io.reactivex.subjects.BehaviorSubject

interface PaginationPort<T> : PresentationPort {
    val loadingPagedItems: BehaviorSubject<Boolean>
    val pagedItemsResult: BehaviorSubject<PaginatedBatch<T>>
}
