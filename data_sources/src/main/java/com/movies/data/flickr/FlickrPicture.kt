package com.movies.data.flickr

import com.google.gson.annotations.SerializedName

class FlickrPicture(
    @SerializedName("id") val id: String? = null,
    @SerializedName("owner") val owner: String? = null,
    @SerializedName("secret") val secret: String? = null,
    @SerializedName("server") val server: String? = null,
    @SerializedName("farm") val farm: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("ispublic") val isPublic: String? = null,
    @SerializedName("isfriend") val isFriend: String? = null,
    @SerializedName("isfamily") val isamily: String? = null,
)
