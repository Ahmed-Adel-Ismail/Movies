package com.movies.core.searching.results

import com.movies.core.entities.Movie
import com.movies.core.entities.PaginatedBatch
import io.reactivex.Single

interface SearchResultsDataSource {
    /**
     * this API never returns error, it puts the error inside the [PaginatedBatch.error]
     */
    fun requestImageUrls(movie: Movie) = Single.error<PaginatedBatch<String>>(NotImplementedError())
}
