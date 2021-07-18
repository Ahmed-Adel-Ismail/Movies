package com.movies.data.flickr

import com.google.gson.annotations.SerializedName

data class FlickrPicturesMetadata(
    @SerializedName("photo") val photos: List<FlickrPicture>? = null
)
