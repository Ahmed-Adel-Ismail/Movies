package com.movies.core

import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

internal const val IMAGES_URLS_ITEMS_PER_PAGE = 100
internal const val IMAGES_URLS_INITIAL_PAGE_NUMBER = 1

/**
 * indicates that an operation should not be blocking to the thread it is executed upon,
 * which means that it is not allowed to communicate with external data sources
 */
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
private annotation class NonBlockingOperation

interface DetailsDataSource {
    @NonBlockingOperation
    fun saveSelectedMovie(movie: Movie) = Unit
    fun loadSelectedMovie() = Single.error<Movie>(NotImplementedError())
    fun requestMovieImagesBatch(batch: PaginatedBatch<String>) =
        Single.error<PaginatedBatch<String>>(NotImplementedError())
}

interface DetailsPort : PresentationPort, PaginationPort<String> {
    val title: BehaviorSubject<String>
    val year: BehaviorSubject<String>
    val genres: BehaviorSubject<List<String>>
    val cast: BehaviorSubject<List<String>>
    val bindDetails: BusinessRule
}

@BusinessRules
fun DetailsPort.bindDetails() = withDisposable {
    DataSources.moviesDetailsDataSource.loadSelectedMovie()
        .filter { it.title != null }
        .switchIfEmpty(Single.error(MissingMovieTitleException))
        .subscribeCatching {
            title.onNext(it.title ?: EMPTY_TEXT)
            year.onNext(it.year ?: EMPTY_TEXT)
            genres.onNext(it.genres ?: listOf())
            cast.onNext(it.cast ?: listOf())
            onLoadMoreImages(PaginatedBatch(it.title))
        }
}

@BusinessRules
fun PresentationPort.onSelectMovie(movie: Movie) {
    DataSources.moviesDetailsDataSource.saveSelectedMovie(movie)
}

@BusinessRules
fun DetailsPort.onLoadMoreImages(batch: PaginatedBatch<String>? = null) = onFetchPagedItems(batch) {
    DataSources.moviesDetailsDataSource.requestMovieImagesBatch(it)
}

object MissingMovieTitleException : RuntimeException()
