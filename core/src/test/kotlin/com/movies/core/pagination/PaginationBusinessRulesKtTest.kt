package com.movies.core.pagination

import com.movies.core.entities.NoMoreResultsException
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

    @Test
    fun `plus(Throwable) then return throwable in PaginatedBatch_error`() {
        val exception = UnsupportedOperationException()
        val value = PaginatedBatch<Int>()
        val result = value + exception
        assertEquals(exception, result.error)
    }

    @Test
    fun `plus(Throwable) then never update PaginatedBatch_pageNumber`() {
        val value = PaginatedBatch<Int>(pageNumber = 1)
        val result = value + UnsupportedOperationException()
        assertEquals(1, result.pageNumber)
    }

    @Test
    fun `plus(List) with null list then return NoMoreResultsException in PaginatedBatch_error`() {
        val items: List<Int>? = null
        val value = PaginatedBatch<Int>()
        val result = value + items
        assertEquals(NoMoreResultsException, result.error)
    }

    @Test
    fun `plus(List) with null list then never update PaginatedBatch_pageNumber`() {
        val items: List<Int>? = null
        val value = PaginatedBatch<Int>(pageNumber = 1)
        val result = value + items
        assertEquals(1, result.pageNumber)
    }

    @Test
    fun `plus(List) with empty list then return NoMoreResultsException in PaginatedBatch_error`() {
        val items: List<Int> = listOf()
        val value = PaginatedBatch<Int>()
        val result = value + items
        assertEquals(NoMoreResultsException, result.error)
    }

    @Test
    fun `plus(List) with empty list then never update PaginatedBatch_pageNumber`() {
        val items: List<Int> = listOf()
        val value = PaginatedBatch<Int>(pageNumber = 1)
        val result = value + items
        assertEquals(1, result.pageNumber)
    }

    @Test
    fun `plus(List) with null PaginatedBatch_pageNumber then return PaginatedBatch_pageNumber as 1`() {
        val items: List<Int> = listOf(1)
        val value = PaginatedBatch<Int>(pageNumber = null)
        val result = value + items
        assertEquals(1, result.pageNumber)
    }

    @Test
    fun `plus(List) with then increment PaginatedBatch_pageNumber`() {
        val items: List<Int> = listOf(1)
        val value = PaginatedBatch<Int>(pageNumber = 1)
        val result = value + items
        assertEquals(2, result.pageNumber)
    }

    @Test
    fun `plus(List) with existing items then append new items to PaginatedBatch_items`() {
        val items: List<Int> = listOf(1)
        val value = PaginatedBatch(items = listOf(1))
        val result = value + items
        assertEquals(listOf(1, 1), result.items)
    }

    @Test
    fun `plus(List) with null items then put new items in PaginatedBatch_items`() {
        val items: List<Int> = listOf(1)
        val value = PaginatedBatch<Int>(items = null)
        val result = value + items
        assertEquals(listOf(1), result.items)
    }
}

class PaginationAdapter<T>(testScheduler: TestScheduler) : PaginationPort<T>,
    PresentationPort by PresentationAdapter(testScheduler) {
    override val loadingPagedItems = BehaviorSubject.create<Boolean>()
    override val pagedItemsResult = BehaviorSubject.create<PaginatedBatch<T>>()
}
