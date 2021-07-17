package com.movies.data.flickr

import com.google.gson.annotations.SerializedName

data class FlickrSearchRequest(
    @SerializedName("movieTitle") val title: String,
    @SerializedName("page") val page: Int = 1,
    @SerializedName("itemsPerPage") val itemsPerPage: Int = 100
)
