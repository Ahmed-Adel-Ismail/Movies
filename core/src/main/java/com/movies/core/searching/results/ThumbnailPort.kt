package com.movies.core.searching.results

import com.movies.core.entities.Movie
import com.movies.core.presentation.PresentationPort
import io.reactivex.subjects.BehaviorSubject

interface ThumbnailPort : PresentationPort {
    val movie: BehaviorSubject<Movie>
    val loadingThumbnailImageUrl: BehaviorSubject<Boolean>
    val thumbnailImageUrl: BehaviorSubject<String>
}
