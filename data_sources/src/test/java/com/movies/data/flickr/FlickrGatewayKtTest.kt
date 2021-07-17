package com.movies.data.flickr

import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class FlickrGatewayKtTest {

    @Test
    fun `requestPictures(FlickrSearch) then call requestPictures(Map) with parameters as Strings`() {
        runBlocking {

            val gateway = object : FlickrGateway {
                override val service: FlickrApiService = mock()
                override val apiKey: String = "A"
            }

            gateway.requestPictures(FlickrSearchRequest(title = "B", page = 1, itemsPerPage = 2))

            verify(gateway.service).requestPictures(
                apiKey = eq("A"),
                title = eq("B"),
                page = eq(1),
                itemsPerPage = eq(2)
            )

        }
    }
}
