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
        val adapter = ThumbnailAdapter(testScheduler)

        adapter.thumbnailImageUrl.onNext("A")

        var result: String? = null
        adapter.onRequestImageUrl {
            result = it
        }
        testScheduler.triggerActions()

        assertEquals("A", result)
    }

    @Test
    fun `onRequestImageUrl() with existing url then never invoke SearchResultsDataSource_requestImageUrl()`() {

        DataSources.moviesSearchResultsDataSource = mock()

        val testScheduler = TestScheduler()
        val adapter = ThumbnailAdapter(testScheduler)

        adapter.thumbnailImageUrl.onNext("A")

        adapter.onRequestImageUrl {
            // do nothing
        }
        testScheduler.triggerActions()

        verify(DataSources.moviesSearchResultsDataSource, never()).requestImageUrl(any())
    }

    @Test
    fun `onRequestImageUrl() with existing url then never update loadingThumbnailImageUrl`() {
        val testScheduler = TestScheduler()
        val adapter = ThumbnailAdapter(testScheduler)

        adapter.thumbnailImageUrl.onNext("A")
        adapter.loadingThumbnailImageUrl
            .test()
            .also { adapter.onRequestImageUrl {} }
            .also { testScheduler.triggerActions() }
            .assertNoValues()
    }

    @Test
    fun `onRequestImageUrl() with no movie then invoke callback with EMPTY_TEXT `() {
        val testScheduler = TestScheduler()
        val adapter = ThumbnailAdapter(testScheduler)

        var result: String? = null
        adapter.onRequestImageUrl {
            result = it
        }
        testScheduler.triggerActions()

        assertEquals(EMPTY_TEXT, result)
    }

    @Test
    fun `onRequestImageUrl() with no movie then never invoke SearchResultsDataSource_requestImageUrl()`() {
        DataSources.moviesSearchResultsDataSource = mock()

        val testScheduler = TestScheduler()
        val adapter = ThumbnailAdapter(testScheduler)

        adapter.onRequestImageUrl {
            // do nothing
        }
        testScheduler.triggerActions()

        verify(DataSources.moviesSearchResultsDataSource, never()).requestImageUrl(any())
    }

    @Test
    fun `onRequestImageUrl() with no movie then never update loadingThumbnailImageUrl`() {
        val testScheduler = TestScheduler()
        val adapter = ThumbnailAdapter(testScheduler)

        adapter.loadingThumbnailImageUrl
            .test()
            .also { adapter.onRequestImageUrl {} }
            .also { testScheduler.triggerActions() }
            .assertNoValues()
    }

    @Test
    fun `onRequestImageUrl() then update loadingThumbnailImageUrl with true `() {

        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrl(any()) } doReturn Single.just("URL")
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))
        adapter.loadingThumbnailImageUrl
            .test()
            .also { adapter.onRequestImageUrl {} }
            .also { testScheduler.triggerActions() }
            .assertValueAt(0, true)
    }

    @Test
    fun `onRequestImageUrl() then invoke SearchResultsDataSource_requestImageUrl()`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrl(any()) } doReturn Single.just("URL")
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))
        adapter.onRequestImageUrl {}
        testScheduler.triggerActions()

        verify(DataSources.moviesSearchResultsDataSource).requestImageUrl(any())
    }

    @Test
    fun `onRequestImageUrl() then update thumbnailImageUrl with result`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrl(any()) } doReturn Single.just("URL")
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))
        adapter.thumbnailImageUrl
            .test()
            .also { adapter.onRequestImageUrl {} }
            .also { testScheduler.triggerActions() }
            .assertValues("URL")
    }

    @Test
    fun `onRequestImageUrl() then update callback with result`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrl(any()) } doReturn Single.just("URL")
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))

        var result: String? = null
        adapter.onRequestImageUrl {
            result = it
        }
        testScheduler.triggerActions()

        assertEquals("URL", result)
    }

    @Test
    fun `onRequestImageUrl() with cancelled operation then never update callback`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrl(any()) } doReturn Single.just("URL")
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))

        var result: String? = null
        val cancellable = adapter.onRequestImageUrl {
            result = it
        }
        cancellable.cancel()
        testScheduler.triggerActions()

        assertNull(result)
    }

    @Test
    fun `onRequestImageUrl() then update loadingThumbnailImageUrl with false`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrl(any()) } doReturn Single.just("URL")
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))
        adapter.loadingThumbnailImageUrl
            .test()
            .also { adapter.onRequestImageUrl {} }
            .also { testScheduler.triggerActions() }
            .assertValueAt(1, false)
    }

    @Test
    fun `onRequestImageUrl() with error then update thumbnailImageUrl with EMPTY_TEXT`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrl(any()) } doReturn Single.error(UnsupportedOperationException())
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))
        adapter.thumbnailImageUrl
            .test()
            .also { adapter.onRequestImageUrl {} }
            .also { testScheduler.triggerActions() }
            .assertValues(EMPTY_TEXT)
    }

    @Test
    fun `onRequestImageUrl() with error then invoke callback with EMPTY_TEXT`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrl(any()) } doReturn Single.error(UnsupportedOperationException())
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))

        var result: String? = null
        adapter.onRequestImageUrl {
            result = it
        }
        testScheduler.triggerActions()

        assertEquals(EMPTY_TEXT, result)
    }

    @Test
    fun `onRequestImageUrl() with error then update loadingThumbnailImageUrl with false`() {
        DataSources.moviesSearchResultsDataSource = mock {
            on { requestImageUrl(any()) } doReturn Single.error(UnsupportedOperationException())
        }

        val testScheduler = TestScheduler()
        val adapter = ThumbnailAdapter(testScheduler)

        adapter.movie.onNext(Movie("A"))
        adapter.loadingThumbnailImageUrl
            .test()
            .also { adapter.onRequestImageUrl {} }
            .also { testScheduler.triggerActions() }
            .assertValueAt(1, false)
    }

    @After
    fun tearDown() {
        DataSources.moviesSearchResultsDataSource = object : SearchResultsDataSource {}
    }
}

class ThumbnailAdapter(testScheduler: TestScheduler) : ThumbnailPort,
    PresentationPort by PresentationAdapter(testScheduler) {
    override val movie = BehaviorSubject.create<Movie>()
    override val loadingThumbnailImageUrl = BehaviorSubject.create<Boolean>()
    override val thumbnailImageUrl = BehaviorSubject.create<String>()
}
