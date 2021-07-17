package com.movies.data.flickr

interface FlickrGateway {

    val service: FlickrApiService

    val apiKey: String

    suspend fun requestPictures(searchRequest: FlickrSearchRequest) = service.requestPictures(
        apiKey = apiKey,
        title = searchRequest.title,
        page = searchRequest.page,
        itemsPerPage = searchRequest.itemsPerPage
    )
}
