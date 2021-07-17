package com.movies.presentation.search.thumbnails

import com.movies.core.entities.Movie
import com.movies.core.searching.results.ThumbnailPort
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class MovieThumbnail(movie: Movie) : ThumbnailPort {
    override val disposables = CompositeDisposable()
    override val scheduler = Schedulers.io()
    override val errors = PublishSubject.create<Throwable>()
    override val movie = BehaviorSubject.createDefault(movie)
    override val loadingThumbnailImageUrl = BehaviorSubject.create<Boolean>()
    override val thumbnailImageUrl = BehaviorSubject.create<String>()
}
