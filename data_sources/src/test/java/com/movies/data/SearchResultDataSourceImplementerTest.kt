package com.movies.data

import com.movies.core.details.MissingMovieTitleException
import com.movies.core.entities.EMPTY_TEXT
import com.movies.core.entities.Movie
import com.movies.data.flickr.FlickrGateway
import com.movies.data.flickr.FlickrPicture
import com.movies.data.flickr.FlickrPicturesMetadata
import com.movies.data.flickr.FlickrSearchRequest
import com.movies.data.flickr.FlickrSearchResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class SearchResultDataSourceImplementerTest {

    @Test(expected = MissingMovieTitleException::class)
    fun `requestImageUrls() with null title then throw MissingMovieTitleException`() {
        val dataSource = SearchResultDataSourceImplementer(mock(), mock())
        dataSource.requestImageUrls(Movie()).blockingGet()
    }

    @Test
    fun `requestImageUrls() then invoke requestPictures() with movie title`() {
        runBlocking {

            val gateway = mock<FlickrGateway> {
                onBlocking { requestPictures(any()) } doReturn FlickrSearchResult(
                    FlickrPicturesMetadata(photos = listOf())
                )
            }
            val dataSource = SearchResultDataSourceImplementer(mock(), gateway)

            dataSource.requestImageUrls(Movie("A")).blockingGet()

            val argumentCapture = argumentCaptor<FlickrSearchRequest> {
                verify(gateway).requestPictures(capture())
            }

            assertEquals("A", argumentCapture.firstValue.title)
        }
    }

    @Test
    fun `requestImageUrls() with successful response then return imageUrls`() {

        val expectedPictures = listOf(
            FlickrPicture(farm = "1A", server = "1B", id = "1C", secret = "1D"),
            FlickrPicture(farm = "2A", server = "2B", id = "2C", secret = "2D"),
            FlickrPicture(farm = "3A", server = "3B", id = "3C", secret = "3D")
        )

        val gateway = mock<FlickrGateway> {
            onBlocking { requestPictures(any()) } doReturn FlickrSearchResult(
                FlickrPicturesMetadata(
                    photos = expectedPictures
                )
            )
        }
        val dataSource = SearchResultDataSourceImplementer(mock(), gateway)

        val result = dataSource.requestImageUrls(Movie("A")).blockingGet()

        assertEquals(expectedPictures.map { it.imageUrl() }, result)
    }

    @Test
    fun `requestImageUrls() with null photos response then return list with EMPTY_TEXT`() {
        val gateway = mock<FlickrGateway> {
            onBlocking { requestPictures(any()) } doReturn FlickrSearchResult(null)
        }
        val dataSource = SearchResultDataSourceImplementer(mock(), gateway)

        val result = dataSource.requestImageUrls(Movie("A")).blockingGet()

        assertEquals(listOf(EMPTY_TEXT), result)
    }

    @Test
    fun `requestImageUrls() with empty response then return list with EMPTY_TEXT`() {
        val gateway = mock<FlickrGateway> {
            onBlocking { requestPictures(any()) } doReturn FlickrSearchResult(
                FlickrPicturesMetadata(
                    photos = listOf()
                )
            )
        }
        val dataSource = SearchResultDataSourceImplementer(mock(), gateway)

        val result = dataSource.requestImageUrls(Movie("A")).blockingGet()

        assertEquals(listOf(EMPTY_TEXT), result)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun `requestImageUrls() with crashing response then propagate error`() {
        val gateway = mock<FlickrGateway> {
            onBlocking { requestPictures(any()) } doThrow UnsupportedOperationException()
        }
        val dataSource = SearchResultDataSourceImplementer(mock(), gateway)

        dataSource.requestImageUrls(Movie("A")).blockingGet()
    }
}
