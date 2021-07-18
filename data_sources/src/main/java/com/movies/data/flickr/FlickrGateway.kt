package com.movies.data.flickr

import com.movies.core.details.MissingMovieTitleException
import com.movies.core.entities.Movie

internal const val FLICKR_DEFAULT_PAGE = 1
internal const val FLICKR_DEFAULT_ITEMS_PER_PAGE = 100

interface FlickrGateway {

    val service: FlickrApiService

    val apiKey: String

    suspend fun requestPictures(searchRequest: FlickrSearchRequest) = service.requestPictures(
        apiKey = apiKey,
        title = searchRequest.title ?: throw MissingMovieTitleException,
        page = searchRequest.page ?: FLICKR_DEFAULT_PAGE,
        itemsPerPage = searchRequest.itemsPerPage ?: FLICKR_DEFAULT_ITEMS_PER_PAGE
    )
}
