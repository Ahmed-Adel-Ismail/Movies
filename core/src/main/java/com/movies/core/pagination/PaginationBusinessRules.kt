package com.movies.core.pagination

import com.movies.core.entities.BusinessRules
import com.movies.core.entities.PaginatedBatch
import com.movies.core.presentation.withDisposable
import io.reactivex.Single

@BusinessRules
internal fun <T> PaginationPort<T>.onFetchPagedItems(
    newBatch: PaginatedBatch<T>? = null,
    requester: (PaginatedBatch<T>) -> Single<PaginatedBatch<T>>
) = withDisposable {
    val finalBatch = detectBatch(newBatch) ?: return@withDisposable null
    loadingPagedItems.onNext(true)
    requester(finalBatch)
        .subscribeOn(scheduler)
        .observeOn(scheduler)
        .doFinally { loadingPagedItems.onNext(false) }
        .subscribeCatching { pagedItemsResult.onNext(it) }
}

private fun <T> PaginationPort<T>.detectBatch(newBatch: PaginatedBatch<T>?) =
    if (newBatch != null) {
        pagedItemsResult.onNext(newBatch)
        newBatch
    } else {
        pagedItemsResult.value
    }
