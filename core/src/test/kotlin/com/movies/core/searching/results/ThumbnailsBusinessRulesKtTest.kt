package com.movies.core.searching.results

import com.movies.core.entities.EMPTY_TEXT
import com.movies.core.entities.Movie
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
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class ThumbnailsBusinessRulesKtTest {

    @Test
    fun `onRequestImageUrl() with existing url then invoke callback `() {

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.thumbnailImageUrls.onNext(listOf("A"))

        var result: List<String>? = null
        adapter.onRequestImageUrl(testScheduler) {
            result = it
        }
        testScheduler.triggerActions()

        assertEquals(listOf("A"), result)
    }

    @Test
    fun `onRequestImageUrl() with existing url then never invoke SearchResultsDataSource_requestImageUrl()`() {

        DataSources.moviesSearchResultsDataSource = mock()

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.thumbnailImageUrls.onNext(listOf("A"))

        adapter.onRequestImageUrl(testScheduler) {
            // do nothing
        }
        testScheduler.triggerActions()

        verify(DataSources.moviesSearchResultsDataSource, never()).requestImageUrls(any())
    }

    @Test
    fun `onRequestImageUrl() with existing url then never update loadingThumbnailImageUrl`() {
        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.thumbnailImageUrls.onNext(listOf("A"))
        adapter.loadingThumbnailImageUrls
            .test()
            .also { adapter.onRequestImageUrl(testScheduler) {} }
            .also { testScheduler.triggerActions() }
            .assertNoValues()
    }

    @Test
    fun `onRequestImageUrl() with no movie then invoke callback with EMPTY_TEXT `() {
        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        var result: List<String>? = null
        adapter.onRequestImageUrl(testScheduler) {
            result = it
        }
        testScheduler.triggerActions()

        assertEquals(listOf(EMPTY_TEXT), result)
    }

    @Test
    fun `onRequestImageUrl() with no movie then never invoke SearchResultsDataSource_requestImageUrl()`() {
        DataSources.moviesSearchResultsDataSource = mock()

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.onRequestImageUrl(testScheduler) {
            // do nothing
        }
        testScheduler.triggerActions()

        verify(DataSources.moviesSearchResultsDataSource, never()).requestImageUrls(any())
    }

    @Test
    fun `onRequestImageUrl() with no movie then never update loadingThumbnailImageUrl`() {
        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.loadingThumbnailImageUrls
            .test()
            .also { adapter.onRequestImageUrl(testScheduler) {} }
            .also { testScheduler.triggerActions() }
            .assertNoValues()
    }

    @Test
    fun `onRequestImageUrl() then update loadingThumbnailImageUrl with true `() {

        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrls(any()) } doReturn Single.just(listOf("URL"))
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))
        adapter.loadingThumbnailImageUrls
            .test()
            .also { adapter.onRequestImageUrl(testScheduler) {} }
            .also { testScheduler.triggerActions() }
            .assertValueAt(0, true)
    }

    @Test
    fun `onRequestImageUrl() then invoke SearchResultsDataSource_requestImageUrl()`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrls(any()) } doReturn Single.just(listOf("URL"))
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))
        adapter.onRequestImageUrl(testScheduler) {}
        testScheduler.triggerActions()

        verify(DataSources.moviesSearchResultsDataSource).requestImageUrls(any())
    }

    @Test
    fun `onRequestImageUrl() then update thumbnailImageUrl with result`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrls(any()) } doReturn Single.just(listOf("URL"))
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))
        adapter.thumbnailImageUrls
            .test()
            .also { adapter.onRequestImageUrl(testScheduler) {} }
            .also { testScheduler.triggerActions() }
            .assertValues(listOf("URL"))
    }

    @Test
    fun `onRequestImageUrl() then update callback with result`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrls(any()) } doReturn Single.just(listOf("URL"))
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))

        var result: List<String>? = null
        adapter.onRequestImageUrl(testScheduler) {
            result = it
        }
        testScheduler.triggerActions()

        assertEquals(listOf("URL"), result)
    }

    @Test
    fun `onRequestImageUrl() with cancelled operation then never update callback`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrls(any()) } doReturn Single.just(listOf("URL"))
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))

        var result: List<String>? = null
        val cancellable = adapter.onRequestImageUrl(testScheduler) {
            result = it
        }
        cancellable.cancel()
        testScheduler.triggerActions()

        assertNull(result)
    }

    @Test
    fun `onRequestImageUrl() then update loadingThumbnailImageUrl with false`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrls(any()) } doReturn Single.just(listOf("URL"))
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))
        adapter.loadingThumbnailImageUrls
            .test()
            .also { adapter.onRequestImageUrl(testScheduler) {} }
            .also { testScheduler.triggerActions() }
            .assertValueAt(1, false)
    }

    @Test
    fun `onRequestImageUrl() with error then update thumbnailImageUrl with EMPTY_TEXT`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrls(any()) } doReturn Single.error(UnsupportedOperationException())
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))
        adapter.thumbnailImageUrls
            .test()
            .also { adapter.onRequestImageUrl(testScheduler) {} }
            .also { testScheduler.triggerActions() }
            .assertValues(listOf(EMPTY_TEXT))
    }

    @Test
    fun `onRequestImageUrl() with error then invoke callback with EMPTY_TEXT`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrls(any()) } doReturn Single.error(UnsupportedOperationException())
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))

        var result: List<String>? = null
        adapter.onRequestImageUrl(testScheduler) {
            result = it
        }
        testScheduler.triggerActions()

        assertEquals(listOf(EMPTY_TEXT), result)
    }

    @Test
    fun `onRequestImageUrl() with error then update loadingThumbnailImageUrl with false`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrls(any()) } doReturn Single.error(UnsupportedOperationException())
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailsAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))
        adapter.loadingThumbnailImageUrls
            .test()
            .also { adapter.onRequestImageUrl(testScheduler) {} }
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
    override val loadingThumbnailImageUrls = BehaviorSubject.create<Boolean>()
    override val thumbnailImageUrls = BehaviorSubject.create<List<String>>()
}
