@file:Suppress("unused")

package com.movies.core.presentation

import com.movies.core.integration.Tracking
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.Subject

@DslMarker
private annotation class PresentationPorts

interface PresentationPort {
    val disposables: CompositeDisposable
    val errors: Subject<Throwable>
    val scheduler: Scheduler

    @PresentationPorts
    fun <T> Observable<T>.subscribeCatching(
        onComplete: () -> Unit = {},
        onSuccess: (T) -> Unit
    ): Disposable = subscribe(onSuccess, ::onError, onComplete)

    @PresentationPorts
    fun <T> Flowable<T>.subscribeCatching(
        onComplete: () -> Unit = {},
        onSuccess: (T) -> Unit
    ): Disposable = subscribe(onSuccess, ::onError, onComplete)

    @PresentationPorts
    fun <T> Single<T>.subscribeCatching(
        onSuccess: (T) -> Unit
    ): Disposable = subscribe(onSuccess, ::onError)

    @PresentationPorts
    fun <T> Maybe<T>.subscribeCatching(
        onComplete: () -> Unit = {},
        onSuccess: (T) -> Unit
    ): Disposable = subscribe(onSuccess, ::onError, onComplete)

    @PresentationPorts
    fun Completable.subscribeCatching(
        onComplete: () -> Unit,
    ): Disposable = subscribe(onComplete, ::onError)

    private fun onError(it: Throwable) {
        Tracking.logger.logError(
            this@PresentationPort.javaClass.simpleName,
            "caught exception in errors stream"
        )
        Tracking.logger.logError(
            this@PresentationPort.javaClass.simpleName,
            it.message ?: it.toString()
        )
        Tracking.logger.logException(it)
        errors.onNext(it)
    }
}

@PresentationPorts
fun PresentationPort.dispose() {
    disposables.dispose()
}

@PresentationPorts
infix fun PresentationPort.withDisposable(action: () -> Disposable?) {
    action()?.also(disposables::add)
}


