package com.movies.data

import android.content.Context
import com.movies.core.details.DetailsDataSource
import com.movies.core.entities.EMPTY_TEXT
import com.movies.core.entities.MovieDetails
import com.movies.core.entities.PaginatedBatch
import com.movies.data.flickr.FLICKR_DEFAULT_ITEMS_PER_PAGE
import com.movies.data.flickr.FLICKR_DEFAULT_PAGE
import com.movies.data.flickr.FlickrGateway
import com.movies.data.flickr.FlickrGatewayImplementer
import com.movies.data.flickr.FlickrSearchRequest
import com.movies.data.flickr.toImagesUrls
import io.reactivex.Single
import kotlinx.coroutines.rx2.rxSingle

class DetailsDataSourceImplementer(
    context: Context,
    private val flickerGateway: FlickrGateway = FlickrGatewayImplementer(context)
) : DetailsDataSource {
    override fun saveSelectedMovie(movieDetails: MovieDetails) {
        InMemoryCache.selectedMovieDetails = movieDetails
    }

    override fun loadSelectedMovie() = Single.fromCallable {
        InMemoryCache.selectedMovieDetails
    }

    override fun requestMovieImagesBatch(batch: PaginatedBatch<String>) = rxSingle<PaginatedBatch<String>> {
        // if (batch.pageNumber == null) handleFirstBatch(batch)
        // else if (batch.pageNumber != null && batch.itemsZAAlklli;)
        // else requestNextBatch(batch)
        TODO()
    }

    private suspend fun handleFirstBatch(batch: PaginatedBatch<String>) =
        if (batch.items == null || batch.items == listOf(EMPTY_TEXT)) {
            requestFirstBatch(batch)
        } else {
            updateCachedBatchPagingDetails(batch)
        }

    private suspend fun requestFirstBatch(batch: PaginatedBatch<String>) =
        flickerGateway.requestPictures(FlickrSearchRequest(batch.key))
            .toImagesUrls()
            .map { batch.copy() }


        private fun updateCachedBatchPagingDetails(batch: PaginatedBatch<String>) =
        batch.copy(pageNumber = FLICKR_DEFAULT_PAGE, itemsPerPage = FLICKR_DEFAULT_ITEMS_PER_PAGE)

    private suspend fun requestNextBatch(batch: PaginatedBatch<String>) =
        flickerGateway.requestPictures(FlickrSearchRequest(batch.key))
            .toImagesUrls()
            .let { newUrls ->

                batch.copy(
                pageNumber = (batch.pageNumber ?: FLICKR_DEFAULT_PAGE) + 1,
                items = mutableListOf<String>().apply{
                    val oldItems = batch.items ?: listOf()
                    addAll(oldItems)


                }
            )
            }
}


