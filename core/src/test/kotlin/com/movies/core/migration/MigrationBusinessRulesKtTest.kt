package com.movies.core.migration

import com.movies.core.integration.DataSources
import com.movies.core.presentation.PresentationAdapter
import com.movies.core.presentation.PresentationPort
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.BehaviorSubject
import org.junit.After
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class MigrationBusinessRulesKtTest {

    @Test
    fun `bindMigration() then update migrationInProgress with true`() {
        val testScheduler = TestScheduler()
        val adapter = MigrationAdapter(testScheduler)

        adapter.migrationInProgress
            .test()
            .also { adapter.bindMigration() }
            .also { testScheduler.triggerActions() }
            .assertValueAt(0, true)
    }

    @Test
    fun `bindMigration() with true isMigrationComplete() then never invoke startMigration()`() {

        DataSources.moviesMigrationDataSource = mock {
            on { isMigrationComplete() } doReturn Single.just(true)
        }

        val testScheduler = TestScheduler()
        val adapter = MigrationAdapter(testScheduler)

        adapter.bindMigration()
        testScheduler.triggerActions()

        verify(DataSources.moviesMigrationDataSource, never()).startMigration()
    }

    @Test
    fun `bindMigration() with true isMigrationComplete() then finally update migrationInProgress with false `() {

        DataSources.moviesMigrationDataSource = mock {
            on { isMigrationComplete() } doReturn Single.just(true)
        }

        val testScheduler = TestScheduler()
        val adapter = MigrationAdapter(testScheduler)

        adapter.migrationInProgress
            .test()
            .also { adapter.bindMigration() }
            .also { testScheduler.triggerActions() }
            .assertValueAt(1, false)
    }

    @Test
    fun `bindMigration() with true isMigrationComplete() then update migrationFinished`() {
        val testScheduler = TestScheduler()
        val adapter = MigrationAdapter(testScheduler)

        DataSources.moviesMigrationDataSource = mock {
            on { isMigrationComplete() } doReturn Single.just(true)
        }

        adapter.migrationFinished
            .test()
            .also { adapter.bindMigration() }
            .also { testScheduler.triggerActions() }
            .assertValues(true)
    }

    @Test
    fun `bindMigration() with false isMigrationComplete() then invoke startMigration()`() {
        val testScheduler = TestScheduler()
        val adapter = MigrationAdapter(testScheduler)

        DataSources.moviesMigrationDataSource = mock {
            on { isMigrationComplete() } doReturn Single.just(false)
            on { startMigration() } doReturn Completable.complete()
        }


        adapter.bindMigration()
        testScheduler.triggerActions()

        verify(DataSources.moviesMigrationDataSource).startMigration()
    }

    @Test
    fun `bindMigration() with crashing isMigrationComplete() then invoke startMigration()`() {
        val testScheduler = TestScheduler()
        val adapter = MigrationAdapter(testScheduler)

        DataSources.moviesMigrationDataSource = mock {
            on { isMigrationComplete() } doReturn Single.error(Exception())
            on { startMigration() } doReturn Completable.complete()
        }


        adapter.bindMigration()
        testScheduler.triggerActions()

        verify(DataSources.moviesMigrationDataSource).startMigration()
    }

    @Test
    fun `bindMigration() with completed startMigration() then update migrationFinished`() {
        val testScheduler = TestScheduler()
        val adapter = MigrationAdapter(testScheduler)

        DataSources.moviesMigrationDataSource = mock {
            on { isMigrationComplete() } doReturn Single.just(false)
            on { startMigration() } doReturn Completable.complete()
        }

        adapter.migrationFinished
            .test()
            .also { adapter.bindMigration() }
            .also { testScheduler.triggerActions() }
            .assertValues(true)
    }

    @Test
    fun `bindMigration() with failing startMigration() then never update migrationFinished`() {
        val testScheduler = TestScheduler()
        val adapter = MigrationAdapter(testScheduler)

        DataSources.moviesMigrationDataSource = mock {
            on { isMigrationComplete() } doReturn Single.just(false)
            on { startMigration() } doReturn Completable.error(UnsupportedOperationException())
        }

        adapter.migrationFinished
            .test()
            .also { adapter.bindMigration() }
            .also { testScheduler.triggerActions() }
            .assertNoValues()
    }

    @Test
    fun `bindMigration() with failing startMigration() then update errors`() {
        val testScheduler = TestScheduler()
        val adapter = MigrationAdapter(testScheduler)

        DataSources.moviesMigrationDataSource = mock {
            on { isMigrationComplete() } doReturn Single.just(false)
            on { startMigration() } doReturn Completable.error(UnsupportedOperationException())
        }

        adapter.errors
            .test()
            .also { adapter.bindMigration() }
            .also { testScheduler.triggerActions() }
            .assertValueCount(1)
            .assertValueAt(0) { it is UnsupportedOperationException }
    }

    @Test
    fun `bindMigration() with failing startMigration() then update migrationInProgress`() {
        val testScheduler = TestScheduler()
        val adapter = MigrationAdapter(testScheduler)

        DataSources.moviesMigrationDataSource = mock {
            on { isMigrationComplete() } doReturn Single.just(false)
            on { startMigration() } doReturn Completable.error(UnsupportedOperationException())
        }

        adapter.migrationInProgress
            .test()
            .also { adapter.bindMigration() }
            .also { testScheduler.triggerActions() }
            .assertValues(true, false)
    }

    @Test
    fun `bindMigration() with error in isMigrationComplete() then never update errors`() {
        val testScheduler = TestScheduler()
        val adapter = MigrationAdapter(testScheduler)

        DataSources.moviesMigrationDataSource = mock {
            on { isMigrationComplete() } doReturn Single.error(UnsupportedOperationException())
            on { startMigration() } doReturn Completable.complete()
        }

        adapter.errors
            .test()
            .also { adapter.bindMigration() }
            .also { testScheduler.triggerActions() }
            .assertNoValues()
    }

    @Test
    fun `bindMigration() with error in startMigration() then update errors`() {
        val testScheduler = TestScheduler()
        val adapter = MigrationAdapter(testScheduler)

        DataSources.moviesMigrationDataSource = mock {
            on { isMigrationComplete() } doReturn Single.just(false)
            on { startMigration() } doReturn Completable.error(UnsupportedOperationException())
        }

        adapter.errors
            .test()
            .also { adapter.bindMigration() }
            .also { testScheduler.triggerActions() }
            .assertValueCount(1)
            .assertValueAt(0) { it is UnsupportedOperationException }
    }

    @After
    fun tearDown() {
        DataSources.moviesMigrationDataSource = object : MigrationDataSource {}
    }
}

private class MigrationAdapter(scheduler: TestScheduler) : MigrationPort,
    PresentationPort by PresentationAdapter(scheduler) {
    override val migrationInProgress = BehaviorSubject.create<Boolean>()
    override val migrationFinished = BehaviorSubject.create<Boolean>()
    override val bindMigration = Unit
}
