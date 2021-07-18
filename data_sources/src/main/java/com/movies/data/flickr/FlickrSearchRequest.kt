package com.movies.data.flickr

import com.google.gson.annotations.SerializedName

data class FlickrSearchRequest(
    @SerializedName("movieTitle") val title: String?,
    @SerializedName("page") val page: Int? = null,
    @SerializedName("itemsPerPage") val itemsPerPage: Int? = null
)
