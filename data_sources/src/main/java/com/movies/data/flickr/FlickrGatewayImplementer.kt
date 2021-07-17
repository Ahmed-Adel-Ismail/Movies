package com.movies.data.flickr

import android.content.Context
import android.content.pm.PackageManager.GET_META_DATA
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BASIC
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://api.flickr.com/services/rest/"
private const val CONNECTION_TIMEOUT_MS: Long = 120
private const val API_KEY_META_DATA = "com.movies.data.flickr-api-key"

class FlickrGatewayImplementer(context: Context) : FlickrGateway {

    override val service: FlickrApiService by lazy { flickerApiService() }

    override val apiKey: String by lazy {
        context.run { packageManager.getApplicationInfo(packageName, GET_META_DATA).metaData }
            .getString(API_KEY_META_DATA)
            ?: throw MissingFlickrApiKeyException
    }

    private fun flickerApiService() = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson()))
        .client(okHttpClient())
        .build()
        .create(FlickrApiService::class.java)

    private fun okHttpClient() = OkHttpClient.Builder()
        .connectTimeout(CONNECTION_TIMEOUT_MS, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply { level = BASIC })
        .build()

    private fun gson() = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()
}

object MissingFlickrApiKeyException : RuntimeException(
    "must include flickr api key as a meta-data in manifest with " +
        "android:name=\"$API_KEY_META_DATA\" and " +
        "android:value=\"API_KEY_VALUE\""
)
