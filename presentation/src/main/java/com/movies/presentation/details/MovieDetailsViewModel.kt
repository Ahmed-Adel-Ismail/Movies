package com.movies.presentation.details

import androidx.lifecycle.ViewModel
import com.movies.core.details.DetailsPort
import com.movies.core.details.bindDetails
import com.movies.core.entities.Movie
import com.movies.core.entities.PaginatedBatch
import com.movies.core.presentation.dispose
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class MovieDetailsViewModel : ViewModel(), DetailsPort {
    override val disposables = CompositeDisposable()
    override val errors = PublishSubject.create<Throwable>()
    override val scheduler = Schedulers.io()
    override val movie = BehaviorSubject.create<Movie>()
    override val loadingPagedItems = BehaviorSubject.create<Boolean>()
    override val pagedItemsResult = BehaviorSubject.create<PaginatedBatch<String>>()
    override val bindDetails = bindDetails()
    override fun onCleared() = dispose()
}
