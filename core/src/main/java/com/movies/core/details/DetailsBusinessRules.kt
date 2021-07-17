package com.movies.core.details

import com.movies.core.entities.BusinessRules
import com.movies.core.entities.EMPTY_TEXT
import com.movies.core.entities.Movie
import com.movies.core.entities.PaginatedBatch
import com.movies.core.integration.DataSources
import com.movies.core.pagination.onFetchPagedItems
import com.movies.core.presentation.PresentationPort
import com.movies.core.presentation.withDisposable
import io.reactivex.Single

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
