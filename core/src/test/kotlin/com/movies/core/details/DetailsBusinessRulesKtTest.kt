package com.movies.core.details

import com.movies.core.entities.Movie
import com.movies.core.entities.MovieDetails
import com.movies.core.entities.PaginatedBatch
import com.movies.core.integration.DataSources
import com.movies.core.searching.results.ThumbnailsAdapter
import com.movies.core.searching.results.ThumbnailsPort
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Test
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class DetailsBusinessRulesKtTest {

    @Test
    fun `onLoadMoreImages() with null imagesUrls then never invoke DetailsDataSource_requestMovieImagesBatch()`() {
        DataSources.moviesDetailsDataSource = mock()

        val testScheduler = TestScheduler()
        val adapter = DetailsAdapter(testScheduler)

        adapter.onLoadMoreImages()
        testScheduler.triggerActions()

        verify(DataSources.moviesDetailsDataSource, never()).requestMovieImagesBatch(anyOrNull())
    }

    @Test
    fun `onLoadMoreImages() then invoke DetailsDataSource_requestMovieImagesBatch()`() {

        DataSources.moviesDetailsDataSource = mock {
            on { requestMovieImagesBatch(anyOrNull()) } doReturn Single.just(PaginatedBatch(key = "A"))
        }

        val testScheduler = TestScheduler()
        val adapter = DetailsAdapter(testScheduler)

        adapter.pagedItemsResult.onNext(PaginatedBatch(key = "A"))
        adapter.onLoadMoreImages()
        testScheduler.triggerActions()


        verify(DataSources.moviesDetailsDataSource).requestMovieImagesBatch(anyOrNull())
    }

    @Test
    fun `onLoadMoreImages() then update imagesUrls`() {

        DataSources.moviesDetailsDataSource = mock {
            on { requestMovieImagesBatch(anyOrNull()) } doReturn Single.just(PaginatedBatch(key = "B"))
        }

        val testScheduler = TestScheduler()
        val adapter = DetailsAdapter(testScheduler)

        adapter.pagedItemsResult.onNext(PaginatedBatch(key = "A"))

        adapter.pagedItemsResult
            .test()
            .also { adapter.onLoadMoreImages() }
            .also { testScheduler.triggerActions() }
            .assertValues(PaginatedBatch(key = "A"), PaginatedBatch(key = "B"))


        verify(DataSources.moviesDetailsDataSource).requestMovieImagesBatch(anyOrNull())
    }

    @Test
    fun `onLoadMoreImages() with error then update errors`() {

        DataSources.moviesDetailsDataSource = mock {
            on { requestMovieImagesBatch(anyOrNull()) } doReturn Single.error(Exception())
        }

        val testScheduler = TestScheduler()
        val adapter = DetailsAdapter(testScheduler)

        adapter.pagedItemsResult.onNext(PaginatedBatch(key = "A"))

        adapter.errors
            .test()
            .also { adapter.onLoadMoreImages() }
            .also { testScheduler.triggerActions() }
            .assertValueCount(1)
            .assertValueAt(0) { it is Exception }


        verify(DataSources.moviesDetailsDataSource).requestMovieImagesBatch(anyOrNull())
    }

    @Test
    fun `bindDetails() then invoke DetailsDataSource_loadSelectedMovie()`() {
        DataSources.moviesDetailsDataSource = mock {
            on { loadSelectedMovie() } doReturn Single.just(
                MovieDetails(Movie("A", "B", listOf("C"), listOf("D")))
            )
        }

        val testScheduler = TestScheduler()
        val adapter = DetailsAdapter(testScheduler)

        adapter.bindDetails()
        testScheduler.triggerActions()

        verify(DataSources.moviesDetailsDataSource).loadSelectedMovie()
    }

    @Test
    fun `bindDetails() with crashing DetailsDataSource_loadSelectedMovie() then update errors`() {
        DataSources.moviesDetailsDataSource = mock {
            on { loadSelectedMovie() } doReturn Single.error(UnsupportedOperationException())
        }

        val testScheduler = TestScheduler()
        val adapter = DetailsAdapter(testScheduler)

        adapter.errors
            .test()
            .also { adapter.bindDetails() }
            .also { testScheduler.triggerActions() }
            .assertValueCount(1)
            .assertValueAt(0) { it is UnsupportedOperationException }
    }

    @Test
    fun `bindDetails() with null movie then throw MissingMovieException`() {
        DataSources.moviesDetailsDataSource = mock {
            on { loadSelectedMovie() } doReturn Single.just(
                MovieDetails()
            )
        }

        val testScheduler = TestScheduler()
        val adapter = DetailsAdapter(testScheduler)

        adapter.errors
            .test()
            .also { adapter.bindDetails() }
            .also { testScheduler.triggerActions() }
            .assertValueCount(1)
            .assertValueAt(0) { it is MissingMovieException }
    }

    @Test
    fun `bindDetails() with cached PaginatedBatch then update pagedItemsResult`() {
        DataSources.moviesDetailsDataSource = mock {
            on { loadSelectedMovie() } doReturn Single.just(
                MovieDetails(
                    Movie("A"),
                    PaginatedBatch("A")
                )
            )
            on { requestMovieImagesBatch(anyOrNull()) } doReturn Single.just(PaginatedBatch("B"))
        }

        val testScheduler = TestScheduler()
        val adapter = DetailsAdapter(testScheduler)

        adapter.pagedItemsResult
            .test()
            .also { adapter.bindDetails() }
            .also { testScheduler.triggerActions() }
            .assertValues(PaginatedBatch("A"))
    }

    @Test
    fun `bindDetails() with no PaginatedBatch then invoke DetailsDataSource_requestMovieImagesBatch() with initial PaginatedBatch`() {
        DataSources.moviesDetailsDataSource = mock {
            on { loadSelectedMovie() } doReturn Single.just(MovieDetails(Movie("A")))
            on { requestMovieImagesBatch(anyOrNull()) } doReturn Single.just(PaginatedBatch("B"))
        }

        val testScheduler = TestScheduler()
        val adapter = DetailsAdapter(testScheduler)

        adapter.bindDetails()
        testScheduler.triggerActions()


        verify(DataSources.moviesDetailsDataSource).requestMovieImagesBatch(eq(PaginatedBatch("A")))
    }

    @After
    fun tearDown() {
        DataSources.moviesDetailsDataSource = object : DetailsDataSource {}
    }
}

class DetailsAdapter(testScheduler: TestScheduler) : DetailsPort,
    ThumbnailsPort by ThumbnailsAdapter(testScheduler) {
    override val bindDetails = Unit
}
