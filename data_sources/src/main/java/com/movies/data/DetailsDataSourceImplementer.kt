package com.movies.data

import android.content.Context
import com.movies.core.details.DetailsDataSource
import com.movies.core.entities.MovieDetails
import com.movies.core.entities.PaginatedBatch
import com.movies.core.pagination.plus
import com.movies.data.flickr.FLICKR_DEFAULT_ITEMS_PER_PAGE
import com.movies.data.flickr.FLICKR_DEFAULT_PAGE
import com.movies.data.flickr.FlickrGateway
import com.movies.data.flickr.FlickrGatewayImplementer
import com.movies.data.flickr.requestPaginatedBatch
import io.reactivex.Single
import kotlinx.coroutines.rx2.rxSingle

class DetailsDataSourceImplementer(
    context: Context,
    private val flickrGateway: FlickrGateway = FlickrGatewayImplementer(context)
) : DetailsDataSource {
    override fun saveSelectedMovie(movieDetails: MovieDetails) {
        InMemoryCache.selectedMovieDetails = movieDetails
    }

    override fun loadSelectedMovie() = Single.fromCallable {
        InMemoryCache.selectedMovieDetails
    }

    override fun requestMovieImagesBatch(batch: PaginatedBatch<String>) = rxSingle {
        runCatching { requestNextBatch(batch) }
            .map { batch + it }
            .getOrElse { batch + it }
    }

    private suspend fun requestNextBatch(batch: PaginatedBatch<String>) =
        flickrGateway.requestPaginatedBatch(
            batch.key,
            batch.pageNumber ?: FLICKR_DEFAULT_PAGE,
            batch.itemsPerPage ?: FLICKR_DEFAULT_ITEMS_PER_PAGE
        )
}




