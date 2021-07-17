package com.movies.data.flickr

import com.google.gson.annotations.SerializedName

data class FlickrSearchResult(
    @SerializedName("photos") val metadata: FlickrPicturesMetadata? = null
)

