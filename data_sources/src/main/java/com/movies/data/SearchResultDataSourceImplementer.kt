package com.movies.data

import android.content.Context
import com.movies.core.details.MissingMovieTitleException
import com.movies.core.entities.EMPTY_TEXT
import com.movies.core.entities.Movie
import com.movies.core.searching.results.SearchResultsDataSource
import com.movies.data.flickr.FlickrGateway
import com.movies.data.flickr.FlickrGatewayImplementer
import com.movies.data.flickr.FlickrPicture
import com.movies.data.flickr.FlickrSearchRequest
import kotlinx.coroutines.rx2.rxSingle
import org.jetbrains.annotations.TestOnly

class SearchResultDataSourceImplementer(
    context: Context,
    private val flickrGateway: FlickrGateway = FlickrGatewayImplementer(context)
) : SearchResultsDataSource {

    override fun requestImageUrls(movie: Movie) = rxSingle {
        FlickrSearchRequest(title(movie))
            .let { flickrGateway.requestPictures(it) }
            .metadata
            ?.photos
            ?.map { it.imageUrl() }
            ?.takeUnless { it.isEmpty() }
            ?: listOf(EMPTY_TEXT)
    }
}

private fun title(movie: Movie): String {
    return movie.title ?: throw MissingMovieTitleException
}

@TestOnly
internal fun FlickrPicture.imageUrl() =
    "http://farm${farm}.static.flickr.com/${server}/${id}_${secret}.jpg"


