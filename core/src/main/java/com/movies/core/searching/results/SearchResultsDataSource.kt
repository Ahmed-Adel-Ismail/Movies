package com.movies.core.searching.results

import com.movies.core.entities.Movie
import io.reactivex.Single

interface SearchResultsDataSource {
    fun requestImageUrls(movie: Movie) = Single.error<List<String>>(NotImplementedError())
}
