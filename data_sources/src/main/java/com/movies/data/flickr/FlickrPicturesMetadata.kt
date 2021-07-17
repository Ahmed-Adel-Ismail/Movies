package com.movies.data.flickr

import com.google.gson.annotations.SerializedName

data class FlickrPicturesMetadata(
    @SerializedName("page") val page: Int? = null,
    @SerializedName("pages") val pages: Int? = null,
    @SerializedName("perpage") val perPage: Int? = null,
    @SerializedName("total") val total: Long? = null,
    @SerializedName("stat") val status: String? = null,
    @SerializedName("photo") val photos: List<FlickrPicture>? = null
)
