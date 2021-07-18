package com.movies.data

import android.content.Context
import com.movies.core.entities.Movie
import com.movies.core.entities.NoMoreResultsException
import com.movies.core.entities.PaginatedBatch
import com.movies.core.searching.results.SearchResultsDataSource
import com.movies.data.flickr.FLICKR_DEFAULT_ITEMS_PER_PAGE
import com.movies.data.flickr.FLICKR_DEFAULT_PAGE
import com.movies.data.flickr.FlickrGateway
import com.movies.data.flickr.FlickrGatewayImplementer
import com.movies.data.flickr.FlickrSearchRequest
import com.movies.data.flickr.toImagesUrls
import kotlinx.coroutines.rx2.rxSingle

class SearchResultDataSourceImplementer(
    context: Context,
    private val flickrGateway: FlickrGateway = FlickrGatewayImplementer(context)
) : SearchResultsDataSource {

    override fun requestImageUrls(movie: Movie) = rxSingle {
        runCatching {
            val request = FlickrSearchRequest(movie.title)
            val results = flickrGateway.requestPictures(request).toImagesUrls()
            if (results.isEmpty()) throw NoMoreResultsException
            results
        }.map {
            PaginatedBatch(
                key = movie.title,
                pageNumber = FLICKR_DEFAULT_PAGE,
                itemsPerPage = FLICKR_DEFAULT_ITEMS_PER_PAGE,
                items = it
            )
        }.getOrElse {
            PaginatedBatch(
                key = movie.title,
                pageNumber = FLICKR_DEFAULT_PAGE,
                itemsPerPage = FLICKR_DEFAULT_ITEMS_PER_PAGE,
                error = it
            )
        }
    }
}
