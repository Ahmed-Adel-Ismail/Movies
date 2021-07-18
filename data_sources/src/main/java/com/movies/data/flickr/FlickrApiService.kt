package com.movies.data.flickr

import org.jetbrains.annotations.TestOnly
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApiService {
    @GET("?method=flickr.photos.search&format=json&nojsoncallback=1")
    suspend fun requestPictures(
        @Query("api_key") apiKey: String,
        @Query("text") title: String,
        @Query("page") page: Int?,
        @Query("per_page") itemsPerPage: Int?
    ): FlickrSearchResult
}

internal fun FlickrSearchResult?.toImagesUrls() = this
    ?.metadata
    ?.photos
    ?.map { it.imageUrl() }
    ?.takeUnless { it.isEmpty() }
    ?: listOf()

@TestOnly
internal fun FlickrPicture.imageUrl() =
    "http://farm${farm}.static.flickr.com/${server}/${id}_${secret}.jpg"
