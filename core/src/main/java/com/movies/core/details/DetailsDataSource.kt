package com.movies.core.details

import com.movies.core.entities.Movie
import com.movies.core.entities.PaginatedBatch
import io.reactivex.Single

interface DetailsDataSource {
    @NonBlockingOperation
    fun saveSelectedMovie(movie: Movie) = Unit
    fun loadSelectedMovie() = Single.error<Movie>(NotImplementedError())
    fun requestMovieImagesBatch(batch: PaginatedBatch<String>) =
        Single.error<PaginatedBatch<String>>(NotImplementedError())
}
