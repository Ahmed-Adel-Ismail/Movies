package com.movies.core.details

import com.movies.core.entities.BusinessRules
import com.movies.core.entities.EMPTY_TEXT
import com.movies.core.entities.PaginatedBatch
import com.movies.core.integration.DataSources
import com.movies.core.pagination.onFetchPagedItems
import com.movies.core.presentation.withDisposable
import io.reactivex.Single

@BusinessRules
fun DetailsPort.bindDetails() = withDisposable {
    DataSources.moviesDetailsDataSource.loadSelectedMovie()
        .filter { it.movie?.title != null }
        .switchIfEmpty(Single.error(MissingMovieTitleException))
        .subscribeCatching {
            if (it.movie == null) throw MissingMovieException
            title.onNext(it.movie.title ?: EMPTY_TEXT)
            year.onNext(it.movie.year ?: EMPTY_TEXT)
            genres.onNext(it.movie.genres ?: listOf())
            cast.onNext(it.movie.cast ?: listOf())
            onLoadMoreImages(PaginatedBatch(it.movie.title, items = it.imagesUrls))
        }
}

@BusinessRules
fun DetailsPort.onLoadMoreImages(batch: PaginatedBatch<String>? = null) = onFetchPagedItems(batch) {
    DataSources.moviesDetailsDataSource.requestMovieImagesBatch(it)
}

object MissingMovieTitleException : RuntimeException()
object MissingMovieException : RuntimeException()
