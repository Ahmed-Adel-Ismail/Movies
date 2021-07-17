package com.movies.core.searching.results

import com.movies.core.entities.Movie
import com.movies.core.presentation.PresentationPort
import io.reactivex.subjects.BehaviorSubject

interface ThumbnailsPort : PresentationPort {
    val movie: BehaviorSubject<Movie>
    val loadingThumbnailImageUrls: BehaviorSubject<Boolean>
    val thumbnailImageUrls: BehaviorSubject<List<String>>
}
