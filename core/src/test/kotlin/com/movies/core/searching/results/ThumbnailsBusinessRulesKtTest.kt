package com.movies.core.searching.results

import com.movies.core.entities.EMPTY_TEXT
import com.movies.core.entities.Movie
import com.movies.core.entities.MovieDetails
import com.movies.core.entities.PaginatedBatch
import com.movies.core.integration.DataSources
import com.movies.core.presentation.PresentationAdapter
import com.movies.core.presentation.PresentationPort
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.BehaviorSubject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class ThumbnailsBusinessRulesKtTest {

    @Test
    fun `onSelectMovie() then invoke DetailsDataSource_saveSelectedMovie()`() {
        val expected = MovieDetails(Movie(title = "A"), PaginatedBatch(items = listOf("A", "B")))

        DataSources.moviesDetailsDataSource = mock()

        val adapter = ThumbnailsAdapter(TestScheduler())
        adapter.movie.onNext(Movie(title = "A"))
        adapter.pagedItemsResult.onNext(PaginatedBatch(items = listOf("A", "B")))
        adapter.onSelectMovie()

        verify(DataSources.moviesDetailsDataSource).saveSelectedMovie(eq(expected))
    }

    @Test
    fun `onRequestInitialImagesUrls() with existing url then invoke callback `() {

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.pagedItemsResult.onNext(PaginatedBatch(items = listOf("A")))

        var result: List<String>? = null
        adapter.onRequestInitialImagesUrls(testScheduler) {
            result = it
        }
        testScheduler.triggerActions()

        assertEquals(listOf("A"), result)
    }

    @Test
    fun `onRequestInitialImagesUrls() with existing url then never invoke SearchResultsDataSource_requestImageUrl()`() {

        DataSources.moviesSearchResultsDataSource = mock()

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.pagedItemsResult.onNext(PaginatedBatch(items = listOf("A")))

        adapter.onRequestInitialImagesUrls(testScheduler) {
            // do nothing
        }
        testScheduler.triggerActions()

        verify(DataSources.moviesSearchResultsDataSource, never()).requestImageUrls(any())
    }

    @Test
    fun `onRequestInitialImagesUrls() with existing url then never update loadingPagedItems`() {
        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.pagedItemsResult.onNext(PaginatedBatch(items = listOf("A")))
        adapter.loadingPagedItems
            .test()
            .also { adapter.onRequestInitialImagesUrls(testScheduler) {} }
            .also { testScheduler.triggerActions() }
            .assertNoValues()
    }

    @Test
    fun `onRequestInitialImagesUrls() with no movie then invoke callback with EMPTY_TEXT `() {
        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        var result: List<String>? = null
        adapter.onRequestInitialImagesUrls(testScheduler) {
            result = it
        }
        testScheduler.triggerActions()

        assertEquals(listOf(EMPTY_TEXT), result)
    }

    @Test
    fun `onRequestInitialImagesUrls() with no movie then never invoke SearchResultsDataSource_requestImageUrl()`() {
        DataSources.moviesSearchResultsDataSource = mock()

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.onRequestInitialImagesUrls(testScheduler) {
            // do nothing
        }
        testScheduler.triggerActions()

        verify(DataSources.moviesSearchResultsDataSource, never()).requestImageUrls(any())
    }

    @Test
    fun `onRequestInitialImagesUrls() with no movie then never update loadingPagedItems`() {
        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.loadingPagedItems
            .test()
            .also { adapter.onRequestInitialImagesUrls(testScheduler) {} }
            .also { testScheduler.triggerActions() }
            .assertNoValues()
    }

    @Test
    fun `onRequestInitialImagesUrls() then update loadingPagedItems with true `() {

        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrls(any()) } doReturn Single.just(PaginatedBatch(items = listOf("URL")))
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))
        adapter.loadingPagedItems
            .test()
            .also { adapter.onRequestInitialImagesUrls(testScheduler) {} }
            .also { testScheduler.triggerActions() }
            .assertValueAt(0, true)
    }

    @Test
    fun `onRequestInitialImagesUrls() then invoke SearchResultsDataSource_requestImageUrl()`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrls(any()) } doReturn Single.just(PaginatedBatch(items = listOf("URL")))
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))
        adapter.onRequestInitialImagesUrls(testScheduler) {}
        testScheduler.triggerActions()

        verify(DataSources.moviesSearchResultsDataSource).requestImageUrls(any())
    }

    @Test
    fun `onRequestInitialImagesUrls() then update pagedItemsResult with result`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrls(any()) } doReturn Single.just(PaginatedBatch(items = listOf("URL")))
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))
        adapter.pagedItemsResult
            .test()
            .also { adapter.onRequestInitialImagesUrls(testScheduler) {} }
            .also { testScheduler.triggerActions() }
            .assertValues(PaginatedBatch(items = listOf("URL")))
    }

    @Test
    fun `onRequestInitialImagesUrls() then update callback with result`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrls(any()) } doReturn Single.just(PaginatedBatch(items = listOf("URL")))
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))

        var result: List<String>? = null
        adapter.onRequestInitialImagesUrls(testScheduler) {
            result = it
        }
        testScheduler.triggerActions()

        assertEquals(listOf("URL"), result)
    }

    @Test
    fun `onRequestInitialImagesUrls() with cancelled operation then never update callback`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrls(any()) } doReturn Single.just(PaginatedBatch(items = listOf("URL")))
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))

        var result: List<String>? = null
        val cancellable = adapter.onRequestInitialImagesUrls(testScheduler) {
            result = it
        }
        cancellable.cancel()
        testScheduler.triggerActions()

        assertNull(result)
    }

    @Test
    fun `onRequestInitialImagesUrls() then update loadingPagedItems with false`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrls(any()) } doReturn Single.just(PaginatedBatch(items = listOf("URL")))
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))
        adapter.loadingPagedItems
            .test()
            .also { adapter.onRequestInitialImagesUrls(testScheduler) {} }
            .also { testScheduler.triggerActions() }
            .assertValueAt(1, false)
    }

    @Test
    fun `onRequestInitialImagesUrls() with error then update pagedItemsResult_error with exception`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrls(any()) } doReturn Single.just(
                PaginatedBatch(error = UnsupportedOperationException())
            )
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))
        adapter.pagedItemsResult
            .test()
            .also { adapter.onRequestInitialImagesUrls(testScheduler) {} }
            .also { testScheduler.triggerActions() }
            .assertValueCount(1)
            .assertValueAt(0) { it.error is UnsupportedOperationException }
    }

    @Test
    fun `onRequestInitialImagesUrls() with error then invoke callback with EMPTY_TEXT`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrls(any()) } doReturn Single.just(
                PaginatedBatch(error = UnsupportedOperationException())
            )
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))

        var result: List<String>? = null
        adapter.onRequestInitialImagesUrls(testScheduler) {
            result = it
        }
        testScheduler.triggerActions()

        assertEquals(listOf(EMPTY_TEXT), result)
    }

    @Test
    fun `onRequestInitialImagesUrls() with error then update loadingPagedItems with false`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrls(any()) } doReturn Single.error(UnsupportedOperationException())
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))
        adapter.loadingPagedItems
            .test()
            .also { adapter.onRequestInitialImagesUrls(testScheduler) {} }
            .also { testScheduler.triggerActions() }
            .assertValueAt(1, false)
    }

    @After
    fun tearDown() {
        DataSources.moviesSearchResultsDataSource = object : SearchResultsDataSource {}
    }
}

class ThumbnailsAdapter(testScheduler: TestScheduler) : ThumbnailsPort,
    PresentationPort by PresentationAdapter(testScheduler) {
    override val movie = BehaviorSubject.create<Movie>()
    override val loadingPagedItems = BehaviorSubject.create<Boolean>()
    override val pagedItemsResult = BehaviorSubject.create<PaginatedBatch<String>>()
}
