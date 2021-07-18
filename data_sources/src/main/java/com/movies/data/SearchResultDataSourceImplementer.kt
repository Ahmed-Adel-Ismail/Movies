package com.movies.data

import android.content.Context
import com.movies.core.entities.Movie
import com.movies.core.entities.PaginatedBatch
import com.movies.core.searching.results.SearchResultsDataSource
import com.movies.data.flickr.FLICKR_DEFAULT_ITEMS_PER_PAGE
import com.movies.data.flickr.FLICKR_DEFAULT_PAGE
import com.movies.data.flickr.FlickrGateway
import com.movies.data.flickr.FlickrGatewayImplementer
import com.movies.data.flickr.requestPaginatedBatch
import kotlinx.coroutines.rx2.rxSingle
import org.jetbrains.annotations.TestOnly

class SearchResultDataSourceImplementer(
    context: Context,
    private val flickrGateway: FlickrGateway = FlickrGatewayImplementer(context)
) : SearchResultsDataSource {

    override fun requestImageUrls(movie: Movie) = rxSingle {
        runCatching { requestInitialBatch(movie) }
            .map { initialPaginatedBatch(movie.title, items = it) }
            .getOrElse { initialPaginatedBatch(movie.title, error = it) }
    }

    private suspend fun requestInitialBatch(movie: Movie) =
        flickrGateway.requestPaginatedBatch(
            movie.title,
            FLICKR_DEFAULT_PAGE,
            FLICKR_DEFAULT_ITEMS_PER_PAGE
        )
}

@TestOnly
internal fun initialPaginatedBatch(
    key: String?,
    items: List<String>? = null,
    error: Throwable? = null
) = PaginatedBatch(
    key = key,
    pageNumber = FLICKR_DEFAULT_PAGE,
    itemsPerPage = FLICKR_DEFAULT_ITEMS_PER_PAGE,
    items = items,
    error = error
)

