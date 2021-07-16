package com.movies.core

import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit.MILLISECONDS

internal const val EMPTY_TEXT = ""
private const val SEARCH_TERM_DEBOUNCE_MILLIS = 500L

interface SearchDataSource {
    fun searchMovies(batch: PaginatedBatch<MoviesSection>) =
        Single.error<PaginatedBatch<MoviesSection>>(NotImplementedError())
}

interface SearchPort : PresentationPort, PaginationPort<MoviesSection> {
    val searchTerm: BehaviorSubject<String>
    val bindSearch: BusinessRule
}

@BusinessRules
fun SearchPort.bindSearch(debounceMillis: Long = SEARCH_TERM_DEBOUNCE_MILLIS) = withDisposable {
    if (searchTerm.value == null) searchTerm.onNext(EMPTY_TEXT)
    searchTerm.share()
        .observeOn(scheduler)
        .debounce(debounceMillis, MILLISECONDS, scheduler)
        .distinctUntilChanged()
        .subscribeCatching { onLoadMoreResults(PaginatedBatch(it)) }
}

@BusinessRules
fun SearchPort.onLoadMoreResults(batch: PaginatedBatch<MoviesSection>? = null) =
    onFetchPagedItems(batch) { DataSources.moviesSearchDataSource.searchMovies(it) }

