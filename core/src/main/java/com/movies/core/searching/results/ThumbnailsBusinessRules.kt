package com.movies.core.searching.results

import com.movies.core.entities.BusinessRules
import com.movies.core.entities.EMPTY_TEXT
import com.movies.core.entities.Movie
import com.movies.core.integration.DataSources
import com.movies.core.presentation.withDisposable
import io.reactivex.functions.Cancellable

@BusinessRules
fun ThumbnailPort.onRequestImageUrl(onUrlReady: (String) -> Unit): Cancellable {

    if (useCurrentUrlIfAvailable(onUrlReady)) return Cancellable { }

    val movie = movie.value
    if (movie == null) {
        onUrlReady(EMPTY_TEXT)
        return Cancellable { }
    }

    val cancellable = CancellableOperation()
    requestUrlFromDataSource(movie, cancellable, onUrlReady)
    return cancellable
}

private class CancellableOperation : Cancellable {
    var isCancelled = false
    override fun cancel() {
        isCancelled = true
    }
}

private fun ThumbnailPort.requestUrlFromDataSource(
    movie: Movie,
    cancellableOperation: CancellableOperation,
    onUrlReady: (String) -> Unit
) = withDisposable {
    loadingThumbnailImageUrl.onNext(true)
    DataSources.moviesSearchResultsDataSource
        .requestImageUrl(movie)
        .subscribeOn(scheduler)
        .observeOn(scheduler)
        .doOnError(errors::onNext)
        .onErrorReturn { EMPTY_TEXT }
        .doFinally { loadingThumbnailImageUrl.onNext(false) }
        .subscribeCatching {
            thumbnailImageUrl.onNext(it)
            if (!cancellableOperation.isCancelled) onUrlReady(it)
        }
}

private fun ThumbnailPort.useCurrentUrlIfAvailable(onUrlReady: (String) -> Unit): Boolean {
    val url = thumbnailImageUrl.value
    if (url != null) {
        onUrlReady(url)
        return true
    }
    return false
}
