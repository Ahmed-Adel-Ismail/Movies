package com.movies.core

import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.BehaviorSubject
import org.junit.After
import org.junit.Test
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class DetailsFeatureKtTest {

    @Test
    fun `onSelectMovie() then invoke DetailsDataSource_saveSelectedMovie()`() {
        val expected = Movie(title = "A")

        DataSources.moviesDetailsDataSource = mock()

        val adapter = DetailsAdapter(TestScheduler())

        adapter.onSelectMovie(expected)

        verify(DataSources.moviesDetailsDataSource).saveSelectedMovie(eq(expected))
    }

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
                Movie("A", "B", listOf("C"), listOf("D"))
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
    fun `bindDetails() with null title then throw MissingMovieTitleException`() {
        DataSources.moviesDetailsDataSource = mock {
            on { loadSelectedMovie() } doReturn Single.just(
                Movie(null, "B", listOf("C"), listOf("D"))
            )
        }

        val testScheduler = TestScheduler()
        val adapter = DetailsAdapter(testScheduler)

        adapter.errors
            .test()
            .also { adapter.bindDetails() }
            .also { testScheduler.triggerActions() }
            .assertValueCount(1)
            .assertValueAt(0) { it is MissingMovieTitleException }
    }

    @Test
    fun `bindDetails() then update title`() {
        DataSources.moviesDetailsDataSource = mock {
            on { loadSelectedMovie() } doReturn Single.just(
                Movie("A", "B", listOf("C"), listOf("D"))
            )
        }

        val testScheduler = TestScheduler()
        val adapter = DetailsAdapter(testScheduler)

        adapter.title
            .test()
            .also { adapter.bindDetails() }
            .also { testScheduler.triggerActions() }
            .assertValues("A")
    }

    @Test
    fun `bindDetails() then update year`() {
        DataSources.moviesDetailsDataSource = mock {
            on { loadSelectedMovie() } doReturn Single.just(
                Movie("A", "B", listOf("C"), listOf("D"))
            )
        }

        val testScheduler = TestScheduler()
        val adapter = DetailsAdapter(testScheduler)

        adapter.year
            .test()
            .also { adapter.bindDetails() }
            .also { testScheduler.triggerActions() }
            .assertValues("B")
    }

    @Test
    fun `bindDetails() with null year then update with EMPTY_TEXT`() {
        DataSources.moviesDetailsDataSource = mock {
            on { loadSelectedMovie() } doReturn Single.just(
                Movie("A", null, listOf("C"), listOf("D"))
            )
        }

        val testScheduler = TestScheduler()
        val adapter = DetailsAdapter(testScheduler)

        adapter.year
            .test()
            .also { adapter.bindDetails() }
            .also { testScheduler.triggerActions() }
            .assertValues(EMPTY_TEXT)
    }

    @Test
    fun `bindDetails() then update genres`() {
        DataSources.moviesDetailsDataSource = mock {
            on { loadSelectedMovie() } doReturn Single.just(
                Movie("A", "B", listOf("C"), listOf("D"))
            )
        }

        val testScheduler = TestScheduler()
        val adapter = DetailsAdapter(testScheduler)

        adapter.genres
            .test()
            .also { adapter.bindDetails() }
            .also { testScheduler.triggerActions() }
            .assertValues(listOf("D"))
    }

    @Test
    fun `bindDetails() with null genres then update with empty list`() {
        DataSources.moviesDetailsDataSource = mock {
            on { loadSelectedMovie() } doReturn Single.just(
                Movie("A", "B", listOf("C"), null)
            )
        }

        val testScheduler = TestScheduler()
        val adapter = DetailsAdapter(testScheduler)

        adapter.genres
            .test()
            .also { adapter.bindDetails() }
            .also { testScheduler.triggerActions() }
            .assertValues(listOf())
    }

    @Test
    fun `bindDetails() then update cast`() {
        DataSources.moviesDetailsDataSource = mock {
            on { loadSelectedMovie() } doReturn Single.just(
                Movie("A", "B", listOf("C"), listOf("D"))
            )
        }

        val testScheduler = TestScheduler()
        val adapter = DetailsAdapter(testScheduler)

        adapter.cast
            .test()
            .also { adapter.bindDetails() }
            .also { testScheduler.triggerActions() }
            .assertValues(listOf("C"))
    }

    @Test
    fun `bindDetails() with null cast then update with empty list`() {
        DataSources.moviesDetailsDataSource = mock {
            on { loadSelectedMovie() } doReturn Single.just(
                Movie("A", "B", null, listOf("D"))
            )
        }

        val testScheduler = TestScheduler()
        val adapter = DetailsAdapter(testScheduler)

        adapter.cast
            .test()
            .also { adapter.bindDetails() }
            .also { testScheduler.triggerActions() }
            .assertValues(listOf())
    }

    @Test
    fun `bindDetails() then invoke DetailsDataSource_requestMovieImagesBatch() with valid PaginatedBatch`() {
        DataSources.moviesDetailsDataSource = mock {
            on { loadSelectedMovie() } doReturn Single.just(Movie("A"))
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
    PresentationPort by PresentationAdapter(testScheduler) {
    override val title = BehaviorSubject.create<String>()
    override val year = BehaviorSubject.create<String>()
    override val genres = BehaviorSubject.create<List<String>>()
    override val cast = BehaviorSubject.create<List<String>>()
    override val pagedItemsResult = BehaviorSubject.create<PaginatedBatch<String>>()
    override val loadingPagedItems = BehaviorSubject.create<Boolean>()
    override val bindDetails = Unit
}
