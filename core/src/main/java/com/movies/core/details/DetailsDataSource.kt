package com.movies.core.details

import com.movies.core.entities.MovieDetails
import com.movies.core.entities.PaginatedBatch
import com.movies.core.searching.results.ThumbnailsPort
import io.reactivex.Single

interface DetailsDataSource {
    @NonBlockingOperation
    fun saveSelectedMovie(movieDetails: MovieDetails) = Unit
    fun loadSelectedMovie() = Single.error<MovieDetails>(NotImplementedError())
    fun requestMovieImagesBatch(batch: PaginatedBatch<String>) =
        Single.error<PaginatedBatch<String>>(NotImplementedError())
}
