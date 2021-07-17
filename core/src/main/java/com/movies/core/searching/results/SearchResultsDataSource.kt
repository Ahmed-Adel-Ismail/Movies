package com.movies.core.searching.results

import com.movies.core.entities.Movie
import io.reactivex.Single

interface SearchResultsDataSource {
    fun requestImageUrl(movie: Movie) = Single.error<String>(NotImplementedError())
}
