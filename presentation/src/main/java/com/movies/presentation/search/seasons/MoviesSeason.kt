package com.movies.presentation.search.seasons

import com.movies.core.entities.MoviesSection
import com.movies.core.presentation.withDisposable
import com.movies.core.searching.results.SeasonPort
import com.movies.core.searching.results.ThumbnailPort
import com.movies.presentation.search.thumbnails.MovieThumbnail
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class MoviesSeason(section: MoviesSection) : SeasonPort {
    override val disposables = CompositeDisposable()
    override val errors = PublishSubject.create<Throwable>()
    override val scheduler = Schedulers.io()
    override val moviesSection = BehaviorSubject.createDefault(section)
    override val moviesThumbnails = BehaviorSubject.create<List<ThumbnailPort>>()

    init {
        bindMoviesThumbnails()
    }
}

internal fun SeasonPort.bindMoviesThumbnails() = withDisposable {
    moviesSection.share().subscribeCatching { section ->
        moviesThumbnails.onNext(section.movies?.map { movie -> MovieThumbnail(movie) } ?: listOf())
    }
}

