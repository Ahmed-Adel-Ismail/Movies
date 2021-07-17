package com.movies.presentation.search.seasons

import com.movies.core.entities.Movie
import com.movies.core.entities.MoviesSection
import com.movies.core.presentation.PresentationPort
import com.movies.core.searching.results.SeasonPort
import com.movies.core.searching.results.ThumbnailsPort
import com.movies.presentation.search.PresentationAdapter
import com.movies.presentation.search.thumbnails.MovieThumbnails
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.BehaviorSubject
import org.junit.Test

class MoviesSeasonTest {
    @Test
    fun `bindMoviesThumbnails() with movies then update moviesThumbnails`() {
        val testScheduler = TestScheduler()
        val adapter = SeasonAdapter(testScheduler)

        adapter.moviesSection.onNext(MoviesSection(movies = listOf(Movie("A"), Movie("B"))))
        adapter.moviesThumbnails
            .test()
            .also { adapter.bindMoviesThumbnails() }
            .also { testScheduler.triggerActions() }
            .assertValueAt(0) {
                it[0].movie.value == Movie("A") && it[1].movie.value == Movie("B")
            }
    }

    @Test
    fun `bindMoviesThumbnails() with null movies then update moviesThumbnails with empty list`() {

        val testScheduler = TestScheduler()
        val adapter = SeasonAdapter(testScheduler)

        adapter.moviesSection.onNext(MoviesSection(movies = null))
        adapter.moviesThumbnails
            .test()
            .also { adapter.bindMoviesThumbnails() }
            .also { testScheduler.triggerActions() }
            .assertValues(listOf())
    }
}

class SeasonAdapter(testScheduler: TestScheduler) : SeasonPort<MovieThumbnails>,
    PresentationPort by PresentationAdapter(testScheduler) {
    override val moviesSection = BehaviorSubject.create<MoviesSection>()
    override val moviesThumbnails = BehaviorSubject.create<List<MovieThumbnails>>()
}

