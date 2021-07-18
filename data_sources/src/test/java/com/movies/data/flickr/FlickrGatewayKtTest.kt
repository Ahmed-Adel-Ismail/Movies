package com.movies.data.flickr

import com.movies.core.entities.NoMoreResultsException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class FlickrGatewayKtTest {

    @Test
    fun `requestPaginatedBatch() then invoke requestPictures()`() {
        runBlocking {
            val gateway = mock<FlickrGateway>()

            runCatching { gateway.requestPaginatedBatch("A", 10, 100) }.getOrNull()

            verify(gateway).requestPictures(any())
        }
    }

    @Test
    fun `requestPaginatedBatch() then return imagesUrls()`() {
        runBlocking {
            val gateway = mock<FlickrGateway>() {
                onBlocking { requestPictures(any()) } doReturn FlickrSearchResult(
                    FlickrPicturesMetadata(
                        listOf(FlickrPicture())
                    )
                )
            }

            val result = gateway.requestPaginatedBatch("A", 10, 100)

            assertEquals(listOf(FlickrPicture().imageUrl()), result)
        }
    }

    @Test(expected = NoMoreResultsException::class)
    fun `requestPaginatedBatch() empty result then throw NoMoreResultsException`() {
        runBlocking {
            val gateway = mock<FlickrGateway>() {
                onBlocking { requestPictures(any()) } doReturn FlickrSearchResult(
                    FlickrPicturesMetadata(
                        listOf()
                    )
                )
            }

            gateway.requestPaginatedBatch("A", 10, 100)

        }
    }

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
                apiKey = eq(gateway.apiKey),
                title = eq("B"),
                page = eq(1),
                itemsPerPage = eq(2)
            )

        }
    }

    @Test
    fun `requestPictures() with null values then call FlickrApiService_requestPictures() with null values`() {
        runBlocking {

            val gateway = object : FlickrGateway {
                override val service: FlickrApiService = mock()
                override val apiKey: String = "A"
            }

            gateway.requestPictures(FlickrSearchRequest(title = "B"))

            verify(gateway.service).requestPictures(
                apiKey = eq(gateway.apiKey),
                title = eq("B"),
                page = eq(null),
                itemsPerPage = eq(null)
            )

        }
    }

    @Test
    fun `toImagesUrls() with valid FlickrSearchResult then return imageUrls`() {

        val photos = listOf(
            FlickrPicture(farm = "1A", server = "1B", id = "1C", secret = "1D"),
            FlickrPicture(farm = "2A", server = "2B", id = "2C", secret = "2D"),
            FlickrPicture(farm = "3A", server = "3B", id = "3C", secret = "3D")
        )

        val searchResult = FlickrSearchResult(
            metadata = FlickrPicturesMetadata(
                photos = photos
            )
        )

        val result = searchResult.toImagesUrls()

        assertEquals(photos.map { it.imageUrl() }, result)
    }

    @Test
    fun `toImagesUrls() with null FlickrSearchResult then return empty list`() {
        val searchResult: FlickrSearchResult? = null

        val result = searchResult.toImagesUrls()

        assertEquals(listOf<String>(), result)
    }

    @Test
    fun `toImagesUrls() with null metadata in FlickrSearchResult then return empty list`() {
        val searchResult = FlickrSearchResult(metadata = null)

        val result = searchResult.toImagesUrls()

        assertEquals(listOf<String>(), result)
    }

    @Test
    fun `toImagesUrls() with null photos in FlickrSearchResult then return empty list `() {
        val searchResult = FlickrSearchResult(metadata = FlickrPicturesMetadata(photos = null))

        val result = searchResult.toImagesUrls()

        assertEquals(listOf<String>(), result)
    }

    @Test
    fun `toImagesUrls() with empty photos list in FlickrSearchResult then return `() {
        val searchResult = FlickrSearchResult(metadata = FlickrPicturesMetadata(photos = listOf()))

        val result = searchResult.toImagesUrls()

        assertEquals(listOf<String>(), result)
    }
}
