package com.movies.core.searching.results

import com.movies.core.entities.MoviesSection
import com.movies.core.presentation.PresentationPort
import io.reactivex.subjects.BehaviorSubject

interface SeasonPort<T : ThumbnailsPort> : PresentationPort {
    val moviesSection: BehaviorSubject<MoviesSection>
    val moviesThumbnails: BehaviorSubject<List<T>>
}

