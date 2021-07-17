package com.movies.core.presentation

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.BehaviorSubject
import org.junit.Assert.assertEquals
import org.junit.Test

class PresentationPortKtTest {

    @Test
    fun `dispose() then invoke disposables_clear()`() {
        val adapter = PresentationAdapter(TestScheduler())

        adapter.dispose()

        assert(adapter.disposables.isDisposed)
    }

    @Test
    fun `withDisposable() then update disposables`() {
        val adapter = PresentationAdapter(TestScheduler())

        adapter withDisposable {
            Disposables.empty()
        }

        assertEquals(1, adapter.disposables.size())
    }
}

class PresentationAdapter(testScheduler: TestScheduler) : PresentationPort {
    override val disposables = CompositeDisposable()
    override val errors = BehaviorSubject.create<Throwable>()
    override val scheduler: TestScheduler = testScheduler
}
