package com.movies.data.flickr

import com.movies.core.details.MissingMovieTitleException
import com.movies.core.entities.NoMoreResultsException

internal const val FLICKR_DEFAULT_PAGE = 1
internal const val FLICKR_DEFAULT_ITEMS_PER_PAGE = 50

interface FlickrGateway {

    val service: FlickrApiService

    val apiKey: String

    suspend fun requestPictures(searchRequest: FlickrSearchRequest) = service.requestPictures(
        apiKey = apiKey,
        title = searchRequest.title ?: throw MissingMovieTitleException,
        page = searchRequest.page,
        itemsPerPage = searchRequest.itemsPerPage
    )
}

internal suspend fun FlickrGateway.requestPaginatedBatch(
    title: String?,
    pageNumber: Int,
    itemsPerPage: Int,
): List<String> {
    val request = FlickrSearchRequest(title, pageNumber, itemsPerPage)
    val results = requestPictures(request).toImagesUrls()
    if (results.isEmpty()) throw NoMoreResultsException
    return results
}
