package com.movies.presentation.search

import com.movies.core.presentation.PresentationPort
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.BehaviorSubject

class PresentationAdapter(testScheduler: TestScheduler) : PresentationPort {
    override val disposables = CompositeDisposable()
    override val errors = BehaviorSubject.create<Throwable>()
    override val scheduler: TestScheduler = testScheduler
}
