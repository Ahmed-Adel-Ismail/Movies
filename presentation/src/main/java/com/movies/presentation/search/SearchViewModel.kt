package com.movies.presentation.search

import androidx.lifecycle.ViewModel
import com.movies.core.entities.MoviesSection
import com.movies.core.entities.PaginatedBatch
import com.movies.core.presentation.dispose
import com.movies.core.presentation.withDisposable
import com.movies.core.searching.SearchPort
import com.movies.core.searching.bindSearch
import com.movies.presentation.search.seasons.MoviesSeason
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

interface SearchSeasonsPort : SearchPort {
    val searchResults: BehaviorSubject<List<MoviesSeason>>
}

class SearchViewModel : ViewModel(), SearchSeasonsPort {
    override val scheduler = Schedulers.io()
    override val disposables = CompositeDisposable()
    override val searchTerm = BehaviorSubject.create<String>()
    override val errors = PublishSubject.create<Throwable>()
    override val loadingPagedItems = BehaviorSubject.create<Boolean>()
    override val pagedItemsResult = BehaviorSubject.create<PaginatedBatch<MoviesSection>>()
    override val bindSearch = bindSearch()
    override val searchResults = BehaviorSubject.create<List<MoviesSeason>>()

    init {
        bindSearchResults()
    }

    override fun onCleared() = dispose()
}

fun SearchSeasonsPort.bindSearchResults() = withDisposable {
    pagedItemsResult.share().observeOn(scheduler).subscribeCatching { batch ->
        searchResults.onNext(batch.items?.map { MoviesSeason(it) } ?: listOf())
    }
}
