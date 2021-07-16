package com.movies.core

import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

interface PaginationPort<T> : PresentationPort {
    val loadingPagedItems: BehaviorSubject<Boolean>
    val pagedItemsResult: BehaviorSubject<PaginatedBatch<T>>
}

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
