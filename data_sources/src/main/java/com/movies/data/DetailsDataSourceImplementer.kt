package com.movies.data

import com.movies.core.details.DetailsDataSource
import com.movies.core.entities.MovieDetails
import com.movies.core.entities.PaginatedBatch
import io.reactivex.Single

class DetailsDataSourceImplementer : DetailsDataSource {
    override fun saveSelectedMovie(movieDetails: MovieDetails) {
        InMemoryCache.selectedMovieDetails = movieDetails
    }

    override fun loadSelectedMovie() = Single.fromCallable {
        InMemoryCache.selectedMovieDetails
    }

    override fun requestMovieImagesBatch(batch: PaginatedBatch<String>): Single<PaginatedBatch<String>> {
        return super.requestMovieImagesBatch(batch)
    }
}
