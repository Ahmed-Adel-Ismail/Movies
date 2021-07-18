package com.movies.core.searching.results

import com.movies.core.entities.BusinessRules
import com.movies.core.entities.EMPTY_TEXT
import com.movies.core.entities.Movie
import com.movies.core.entities.MovieDetails
import com.movies.core.integration.DataSources
import com.movies.core.presentation.withDisposable
import io.reactivex.Scheduler
import io.reactivex.functions.Cancellable

@BusinessRules
fun ThumbnailsPort.onSelectMovie() {
    DataSources.moviesDetailsDataSource.saveSelectedMovie(
        MovieDetails(movie.value, pagedItemsResult.value)
    )
}

@BusinessRules
fun ThumbnailsPort.onRequestInitialImagesUrls(
    callbackSchedulers: Scheduler,
    onUrlsReady: (List<String>) -> Unit
): Cancellable {

    if (useCurrentUrlIfAvailable(onUrlsReady)) return Cancellable { }

    val movie = movie.value
    if (movie == null) {
        onUrlsReady(listOf(EMPTY_TEXT))
        return Cancellable { }
    }

    val cancellable = CancellableOperation()
    requestUrlFromDataSource(movie, cancellable, callbackSchedulers, onUrlsReady)
    return cancellable
}

private class CancellableOperation : Cancellable {
    var isCancelled = false
    override fun cancel() {
        isCancelled = true
    }
}

private fun ThumbnailsPort.requestUrlFromDataSource(
    movie: Movie,
    cancellableOperation: CancellableOperation,
    callbackScheduler: Scheduler,
    onUrlReady: (List<String>) -> Unit
) = withDisposable {
    loadingPagedItems.onNext(true)
    DataSources.moviesSearchResultsDataSource
        .requestImageUrls(movie)
        .subscribeOn(scheduler)
        .observeOn(scheduler)
        .doOnSuccess { it.error?.also(::updateErrors) }
        .doOnSuccess { pagedItemsResult.onNext(it) }
        .doFinally { loadingPagedItems.onNext(false) }
        .observeOn(callbackScheduler)
        .subscribeCatching {
            if (!cancellableOperation.isCancelled) onUrlReady(it.items ?: listOf(EMPTY_TEXT))
        }
}

private fun ThumbnailsPort.useCurrentUrlIfAvailable(onUrlsReady: (List<String>) -> Unit): Boolean {
    val batch = pagedItemsResult.value
    if (batch?.items != null) {
        onUrlsReady(batch.items)
        return true
    }
    return false
}
