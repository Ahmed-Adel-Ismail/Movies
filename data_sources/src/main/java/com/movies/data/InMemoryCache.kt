package com.movies.data

import com.movies.core.entities.MovieDetails
import com.movies.core.entities.MoviesSection

internal object InMemoryCache {
    var moviesSections = listOf<MoviesSection>()
    var selectedMovieDetails: MovieDetails? = null
}
