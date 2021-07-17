package com.movies.core.searching

import com.movies.core.entities.EMPTY_TEXT
import com.movies.core.entities.MoviesSection
import com.movies.core.entities.PaginatedBatch
import com.movies.core.integration.DataSources
import com.movies.core.presentation.PresentationAdapter
import com.movies.core.presentation.PresentationPort
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.BehaviorSubject
import org.junit.After
import org.junit.Test
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class SearchBusinessRulesKtTest {

    @Test
    fun `bindSearch() then update searchTerm with EMPTY_TEXT`() {
        val testScheduler = TestScheduler()
        val adapter = SearchAdapter(testScheduler)

        adapter.searchTerm
            .test()
            .also { adapter.bindSearch(0) }
            .also { testScheduler.triggerActions() }
            .assertValueAt(0, EMPTY_TEXT)
    }

    @Test
    fun `bindSearch() with new empty text then update searchInProgress with true`() {
        val testScheduler = TestScheduler()
        val adapter = SearchAdapter(testScheduler)

        adapter.loadingPagedItems
            .test()
            .also { adapter.bindSearch(0) }
            .also { testScheduler.triggerActions() }
            .assertValueAt(0, true)
    }

    @Test
    fun `bindSearch() with new non-empty text then update searchInProgress with true`() {
        val testScheduler = TestScheduler()
        val adapter = SearchAdapter(testScheduler)

        adapter.searchTerm.onNext("A")
        adapter.loadingPagedItems
            .test()
            .also { adapter.bindSearch(0) }
            .also { testScheduler.triggerActions() }
            .assertValueAt(0, true)
    }

    @Test
    fun `bindSearch() with new text then invoke SearchDataSource_searchMovies()`() {
        val testScheduler = TestScheduler()
        val adapter = SearchAdapter(testScheduler)

        DataSources.moviesSearchDataSource = mock {
            on { searchMovies(anyOrNull()) } doReturn Single.just(
                PaginatedBatch(items = listOf(MoviesSection()))
            )
        }

        adapter.bindSearch(0)
        testScheduler.triggerActions()

        verify(DataSources.moviesSearchDataSource).searchMovies(anyOrNull())
    }

    @Test
    fun `bindSearch() with one SearchDataSource_searchMovies() result then update searchResult`() {
        val expected = PaginatedBatch(
            items = listOf(MoviesSection(year = "A"), MoviesSection(year = "B"))
        )

        val testScheduler = TestScheduler()
        val adapter = SearchAdapter(testScheduler)

        DataSources.moviesSearchDataSource = mock {
            on { searchMovies(anyOrNull()) } doReturn Single.just(expected)
        }

        adapter.pagedItemsResult
            .map { it.items ?: listOf() }
            .test()
            .also { adapter.bindSearch(0) }
            .also { testScheduler.triggerActions() }
            .assertValues(listOf(), expected.items)
    }

    @Test
    fun `bindSearch() with completed SearchDataSource_searchMovies() then update searchInProgress with false`() {

        val testScheduler = TestScheduler()
        val adapter = SearchAdapter(testScheduler)

        DataSources.moviesSearchDataSource = mock {
            on { searchMovies(anyOrNull()) } doReturn Single.just(
                PaginatedBatch(items = listOf(MoviesSection(year = "A")))
            )
        }

        adapter.loadingPagedItems
            .test()
            .also { adapter.bindSearch(0) }
            .also { testScheduler.triggerActions() }
            .assertValues(true, false)
    }

    @Test
    fun `bindSearch() with error SearchDataSource_searchMovies() then update errors`() {
        val testScheduler = TestScheduler()
        val adapter = SearchAdapter(testScheduler)

        DataSources.moviesSearchDataSource = mock {
            on { searchMovies(anyOrNull()) } doReturn Single.error(UnsupportedOperationException())
        }

        adapter.errors
            .test()
            .also { adapter.bindSearch(0) }
            .also { testScheduler.triggerActions() }
            .assertValueCount(1)
            .assertValueAt(0) { it is UnsupportedOperationException }
    }

    @Test
    fun `bindSearch() with error SearchDataSource_searchMovies() then update searchInProgress with false`() {
        val testScheduler = TestScheduler()
        val adapter = SearchAdapter(testScheduler)

        DataSources.moviesSearchDataSource = mock {
            on { searchMovies(anyOrNull()) } doReturn Single.error(UnsupportedOperationException())
        }

        adapter.loadingPagedItems
            .test()
            .also { adapter.bindSearch(0) }
            .also { testScheduler.triggerActions() }
            .assertValues(true, false)
    }

    @Test
    fun `bindSearch() with error SearchDataSource_searchMovies() then don't end searchTerm stream`() {
        val testScheduler = TestScheduler()
        val adapter = SearchAdapter(testScheduler)

        DataSources.moviesSearchDataSource = mock {
            on { searchMovies(anyOrNull()) } doReturn Single.error(UnsupportedOperationException())
        }

        adapter.searchTerm.onNext("A")
        adapter.loadingPagedItems
            .test()
            .also { adapter.bindSearch(0) }
            .also { testScheduler.triggerActions() }
            .also { adapter.searchTerm.onNext("B") }
            .also { testScheduler.triggerActions() }
            .assertValues(true, false, true, false)
    }

    @Test
    fun `bindSearch() with duplicate text then invoke SearchDataSource_searchMovies() only once`() {
        val testScheduler = TestScheduler()
        val adapter = SearchAdapter(testScheduler)

        DataSources.moviesSearchDataSource = mock {
            on { searchMovies(anyOrNull()) } doReturn Single.just(
                PaginatedBatch(items = listOf(MoviesSection()))
            )
        }

        adapter.searchTerm.onNext("A")
        adapter.bindSearch(0)
        testScheduler.triggerActions()

        adapter.searchTerm.onNext("A")
        testScheduler.triggerActions()

        adapter.searchTerm.onNext("A")
        testScheduler.triggerActions()

        verify(DataSources.moviesSearchDataSource, times(1)).searchMovies(anyOrNull())
    }

    @Test
    fun `bindSearch() with different text then invoke SearchDataSource_searchMovies() each time`() {
        val testScheduler = TestScheduler()
        val adapter = SearchAdapter(testScheduler)

        DataSources.moviesSearchDataSource = mock {
            on { searchMovies(anyOrNull()) } doReturn Single.just(
                PaginatedBatch(items = listOf(MoviesSection()))
            )
        }

        adapter.searchTerm.onNext("A")
        adapter.bindSearch(0)
        testScheduler.triggerActions()

        adapter.searchTerm.onNext("B")
        testScheduler.triggerActions()

        adapter.searchTerm.onNext("C")
        testScheduler.triggerActions()

        verify(DataSources.moviesSearchDataSource, times(3)).searchMovies(anyOrNull())
    }

    @Test
    fun `onLoadMoreResults() with batch then invoke SearchDataSource_searchMovies() with batch`() {
        val testScheduler = TestScheduler()
        val adapter = SearchAdapter(testScheduler)

        DataSources.moviesSearchDataSource = mock {
            on { searchMovies(anyOrNull()) } doReturn Single.just(PaginatedBatch())
        }

        adapter.onLoadMoreResults(PaginatedBatch("A"))
        testScheduler.triggerActions()

        verify(DataSources.moviesSearchDataSource).searchMovies(eq(PaginatedBatch("A")))
    }

    @Test
    fun `onLoadMoreResults() with null batch then invoke SearchDataSource_searchMovies() with null`() {
        val testScheduler = TestScheduler()
        val adapter = SearchAdapter(testScheduler)

        DataSources.moviesSearchDataSource = mock {
            on { searchMovies(anyOrNull()) } doReturn Single.just(PaginatedBatch())
        }

        adapter.pagedItemsResult.onNext(PaginatedBatch("A"))
        adapter.onLoadMoreResults()
        testScheduler.triggerActions()

        verify(DataSources.moviesSearchDataSource).searchMovies(eq(PaginatedBatch("A")))
    }

    @After
    fun tearDown() {
        DataSources.moviesSearchDataSource = object : SearchDataSource {}
    }
}

class SearchAdapter(testScheduler: TestScheduler) : SearchPort,
    PresentationPort by PresentationAdapter(testScheduler) {
    override val searchTerm = BehaviorSubject.create<String>()
    override val loadingPagedItems = BehaviorSubject.create<Boolean>()
    override val pagedItemsResult = BehaviorSubject.create<PaginatedBatch<MoviesSection>>()
    override val bindSearch = Unit
}
