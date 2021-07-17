package com.movies.core.searching

import com.movies.core.entities.BusinessRules
import com.movies.core.entities.EMPTY_TEXT
import com.movies.core.entities.MoviesSection
import com.movies.core.entities.PaginatedBatch
import com.movies.core.entities.SEARCH_TERM_DEBOUNCE_MILLIS
import com.movies.core.integration.DataSources
import com.movies.core.pagination.onFetchPagedItems
import com.movies.core.presentation.withDisposable
import java.util.concurrent.TimeUnit.MILLISECONDS

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



