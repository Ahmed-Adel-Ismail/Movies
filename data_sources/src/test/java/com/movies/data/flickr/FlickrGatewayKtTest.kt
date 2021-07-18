package com.movies.data.flickr

import com.movies.core.details.MissingMovieTitleException
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class FlickrGatewayKtTest {

    @Test(expected = MissingMovieTitleException::class)
    fun `requestPictures() with null title then throw MissingMovieTitleException`() {
        runBlocking {

            val gateway = object : FlickrGateway {
                override val service: FlickrApiService = mock()
                override val apiKey: String = "A"
            }

            gateway.requestPictures(FlickrSearchRequest(title = null, page = 1, itemsPerPage = 2))
        }
    }

    @Test
    fun `requestPictures() then call FlickrApiService_requestPictures() with parameters`() {
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

    @Test
    fun `requestPictures() with null values then call FlickrApiService_requestPictures() with default values`() {
        runBlocking {

            val gateway = object : FlickrGateway {
                override val service: FlickrApiService = mock()
                override val apiKey: String = "A"
            }

            gateway.requestPictures(FlickrSearchRequest(title = "B"))

            verify(gateway.service).requestPictures(
                apiKey = eq("A"),
                title = eq("B"),
                page = eq(FLICKR_DEFAULT_PAGE),
                itemsPerPage = eq(FLICKR_DEFAULT_ITEMS_PER_PAGE)
            )

        }
    }
}
