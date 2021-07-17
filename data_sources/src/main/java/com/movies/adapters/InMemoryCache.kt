package com.movies.adapters

import com.movies.core.entities.MoviesSection

internal object InMemoryCache {
    var moviesSections = listOf<MoviesSection>()
}
