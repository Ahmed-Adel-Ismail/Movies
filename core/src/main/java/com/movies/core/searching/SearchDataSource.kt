package com.movies.core.searching

import com.movies.core.entities.MoviesSection
import com.movies.core.entities.PaginatedBatch
import io.reactivex.Single

interface SearchDataSource {
    fun searchMovies(batch: PaginatedBatch<MoviesSection>) =
        Single.error<PaginatedBatch<MoviesSection>>(NotImplementedError())
}
