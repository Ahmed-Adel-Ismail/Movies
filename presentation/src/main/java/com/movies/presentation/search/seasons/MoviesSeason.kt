package com.movies.presentation.search.seasons

import androidx.lifecycle.ViewModel
import com.movies.core.entities.MoviesSection
import com.movies.core.presentation.dispose
import com.movies.core.presentation.withDisposable
import com.movies.core.searching.results.SeasonPort
import com.movies.presentation.search.thumbnails.MovieThumbnails
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class MoviesSeason(section: MoviesSection) : ViewModel(), SeasonPort<MovieThumbnails> {
    override val disposables = CompositeDisposable()
    override val errors = PublishSubject.create<Throwable>()
    override val scheduler = Schedulers.io()
    override val moviesSection = BehaviorSubject.createDefault(section)
    override val moviesThumbnails = BehaviorSubject.create<List<MovieThumbnails>>()

    init {
        bindMoviesThumbnails()
    }

    public override fun onCleared() {
        moviesThumbnails.share().subscribeCatching { thumbnails -> clearThumbnails(thumbnails) }
        dispose()
    }

    private fun clearThumbnails(thumbnails: List<MovieThumbnails>) {
        thumbnails.forEach { it.dispose() }
    }
}

internal fun SeasonPort<MovieThumbnails>.bindMoviesThumbnails() = withDisposable {
    moviesSection.share().subscribeCatching { section ->
        moviesThumbnails.onNext(section.movies?.map { movie -> MovieThumbnails(movie) } ?: listOf())
    }
}

