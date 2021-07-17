package com.movies.core.pagination

import com.movies.core.entities.PaginatedBatch
import com.movies.core.presentation.PresentationAdapter
import com.movies.core.presentation.PresentationPort
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.BehaviorSubject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PaginationBusinessRulesKtTest {

    @Test
    fun `onFetchPagedItems() with newBatch then update pagedItemsResult`() {
        val data = PaginatedBatch(items = listOf(1, 2))
        val expected = PaginatedBatch(items = listOf(10, 20))
        val testScheduler = TestScheduler()
        val adapter = PaginationAdapter<Int>(testScheduler)

        adapter.pagedItemsResult
            .test()
            .also { adapter.onFetchPagedItems(expected) { Single.just(data) } }
            .also { testScheduler.triggerActions() }
            .assertValueAt(0, expected)
    }

    @Test
    fun `onFetchPagedItems() with null newBatch then invoke requester with pagedItemsResult_value`() {

        var result: PaginatedBatch<Int>? = null

        val expected = PaginatedBatch(items = listOf(1, 2))
        val testScheduler = TestScheduler()
        val adapter = PaginationAdapter<Int>(testScheduler)

        adapter.pagedItemsResult.onNext(expected)
        adapter.onFetchPagedItems { result = it; Single.just(PaginatedBatch()) }
        testScheduler.triggerActions()

        assertEquals(expected, result)
    }

    @Test
    fun `onFetchPagedItems() with null newBatch and null pagedItemsResult_value then never invoke requester`() {

        var result: PaginatedBatch<Int>? = null

        val testScheduler = TestScheduler()
        val adapter = PaginationAdapter<Int>(testScheduler)

        adapter.onFetchPagedItems { result = it; Single.just(PaginatedBatch()) }
        testScheduler.triggerActions()

        assertNull(result)
    }

    @Test
    fun `onFetchPagedItems() with null newBatch then update pagedItemsResult`() {
        val testScheduler = TestScheduler()
        val adapter = PaginationAdapter<Int>(testScheduler)

        adapter.pagedItemsResult.onNext(PaginatedBatch("A"))
        adapter.pagedItemsResult
            .test()
            .also { adapter.onFetchPagedItems { Single.just(PaginatedBatch("B")) } }
            .also { testScheduler.triggerActions() }
            .assertValues(PaginatedBatch("A"), PaginatedBatch("B"))
    }

    @Test
    fun `onFetchPagedItems() then update loadingPagedItems`() {
        val testScheduler = TestScheduler()
        val adapter = PaginationAdapter<Int>(testScheduler)

        adapter.loadingPagedItems
            .test()
            .also { adapter.onFetchPagedItems(PaginatedBatch()) { Single.just(PaginatedBatch()) } }
            .also { testScheduler.triggerActions() }
            .assertValues(true, false)
    }

    @Test
    fun `onFetchPagedItems() wih error then update loadingPagedItems`() {

        val testScheduler = TestScheduler()
        val adapter = PaginationAdapter<Int>(testScheduler)

        adapter.loadingPagedItems
            .test()
            .also { adapter.onFetchPagedItems(PaginatedBatch()) { Single.error(Exception()) } }
            .also { testScheduler.triggerActions() }
            .assertValues(true, false)
    }

    @Test
    fun `onFetchPagedItems() wih error then update errors`() {

        val testScheduler = TestScheduler()
        val adapter = PaginationAdapter<Int>(testScheduler)

        adapter.errors
            .test()
            .also { adapter.onFetchPagedItems(PaginatedBatch()) { Single.error(Exception()) } }
            .also { testScheduler.triggerActions() }
            .assertValueCount(1)
            .assertValueAt(0) { it is Exception }
    }
}

class PaginationAdapter<T>(testScheduler: TestScheduler) : PaginationPort<T>,
    PresentationPort by PresentationAdapter(testScheduler) {
    override val loadingPagedItems = BehaviorSubject.create<Boolean>()
    override val pagedItemsResult = BehaviorSubject.create<PaginatedBatch<T>>()
}
