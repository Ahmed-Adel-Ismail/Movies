package com.movies.presentation.search

import com.movies.core.entities.MoviesSection
import com.movies.core.entities.PaginatedBatch
import com.movies.core.presentation.PresentationPort
import com.movies.core.searching.SearchPort
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.BehaviorSubject

class SearchAdapter(testScheduler: TestScheduler) : SearchPort,
    PresentationPort by PresentationAdapter(testScheduler) {
    override val searchTerm = BehaviorSubject.create<String>()
    override val loadingPagedItems = BehaviorSubject.create<Boolean>()
    override val pagedItemsResult = BehaviorSubject.create<PaginatedBatch<MoviesSection>>()
    override val bindSearch = Unit
}
