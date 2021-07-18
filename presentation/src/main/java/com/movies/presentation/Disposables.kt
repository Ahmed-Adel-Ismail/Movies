package com.movies.presentation

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

@DslMarker
private annotation class Disposables

@Disposables
fun <T> Observable<T>.subscribeWithLifecycle(
    lifecycleOwner: LifecycleOwner,
    releaseOnLifecycleEvent: Lifecycle.Event = Lifecycle.Event.ON_DESTROY,
    onError: (Throwable) -> Unit = Throwable::printStackTrace,
    onComplete: () -> Unit = {},
    onNext: (T) -> Unit
) {
    val disposable = disposable(onNext, onError, onComplete)
    lifecycleOwner.lifecycle.addObserver(lifecycleObserver(releaseOnLifecycleEvent, disposable))
}

private fun <T> Observable<T>.disposable(
    onNext: (T) -> Unit,
    onError: (Throwable) -> Unit,
    onComplete: () -> Unit
) = share()
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(onNext, onError, onComplete)

private fun lifecycleObserver(
    releaseOnLifecycleEvent: Lifecycle.Event,
    disposable: Disposable
) = object : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        if (releaseOnLifecycleEvent == Lifecycle.Event.ON_PAUSE) {
            if (!disposable.isDisposed) disposable.dispose()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        if (releaseOnLifecycleEvent == Lifecycle.Event.ON_STOP) {
            if (!disposable.isDisposed) disposable.dispose()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        if (releaseOnLifecycleEvent == Lifecycle.Event.ON_DESTROY) {
            if (!disposable.isDisposed) disposable.dispose()
        }
    }
}
