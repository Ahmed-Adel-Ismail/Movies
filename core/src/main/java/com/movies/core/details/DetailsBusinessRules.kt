package com.movies.core.details

import com.movies.core.entities.BusinessRules
import com.movies.core.entities.PaginatedBatch
import com.movies.core.integration.DataSources
import com.movies.core.pagination.onFetchPagedItems
import com.movies.core.presentation.withDisposable
import io.reactivex.Single

@BusinessRules
fun DetailsPort.bindDetails() = withDisposable {
    DataSources.moviesDetailsDataSource.loadSelectedMovie()
        .filter { it.movie?.title != null }
        .switchIfEmpty(Single.error(MissingMovieException))
        .subscribeCatching {
            if (it.movie == null) throw MissingMovieException
            movie.onNext(it.movie)
            if (it.imagesUrls != null) pagedItemsResult.onNext(it.imagesUrls)
            else onLoadMoreImages(PaginatedBatch(it.movie.title))
        }
}

@BusinessRules
fun DetailsPort.onLoadMoreImages(batch: PaginatedBatch<String>? = null) = onFetchPagedItems(batch) {
    DataSources.moviesDetailsDataSource.requestMovieImagesBatch(it)
}


object MissingMovieException : RuntimeException()
