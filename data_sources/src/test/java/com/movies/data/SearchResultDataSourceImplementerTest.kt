package com.movies.data

import com.movies.core.entities.Movie
import com.movies.core.entities.NoMoreResultsException
import com.movies.core.entities.PaginatedBatch
import com.movies.data.flickr.FLICKR_DEFAULT_ITEMS_PER_PAGE
import com.movies.data.flickr.FLICKR_DEFAULT_PAGE
import com.movies.data.flickr.FlickrGateway
import com.movies.data.flickr.FlickrPicture
import com.movies.data.flickr.FlickrPicturesMetadata
import com.movies.data.flickr.FlickrSearchRequest
import com.movies.data.flickr.FlickrSearchResult
import com.movies.data.flickr.imageUrl
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
    fun `requestImageUrls() then invoke requestPictures() with FLICKR_DEFAULT_PAGE`() {
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

            assertEquals(FLICKR_DEFAULT_PAGE, argumentCapture.firstValue.page)
        }
    }

    @Test
    fun `requestImageUrls() with successful response then return imageUrls in PaginatedBatch`() {

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

        assertEquals(expectedPictures.map { it.imageUrl() }, result.items)
    }

    @Test
    fun `requestImageUrls() with null photos response then return NoMoreResultsException in error`() {
        val gateway = mock<FlickrGateway> {
            onBlocking { requestPictures(any()) } doReturn FlickrSearchResult(
                FlickrPicturesMetadata(
                    photos = null
                )
            )
        }
        val dataSource = SearchResultDataSourceImplementer(mock(), gateway)

        val result = dataSource.requestImageUrls(Movie("A")).blockingGet()

        assertEquals(NoMoreResultsException, result.error)
    }

    @Test
    fun `requestImageUrls() with empty response then return error as NoMoreResultsException`() {
        val gateway = mock<FlickrGateway> {
            onBlocking { requestPictures(any()) } doReturn FlickrSearchResult(
                FlickrPicturesMetadata(
                    photos = listOf()
                )
            )
        }
        val dataSource = SearchResultDataSourceImplementer(mock(), gateway)

        val result = dataSource.requestImageUrls(Movie("A")).blockingGet()

        assertEquals(NoMoreResultsException, result.error)
    }

    @Test
    fun `requestImageUrls() with failing response then return error in PaginatedBatch`() {
        val exception = UnsupportedOperationException()
        val gateway = mock<FlickrGateway> {
            onBlocking { requestPictures(any()) } doThrow exception
        }
        val dataSource = SearchResultDataSourceImplementer(mock(), gateway)

        val result = dataSource.requestImageUrls(Movie("A")).blockingGet()

        assertEquals(exception, result.error)
    }

    @Test
    fun `initialPaginatedBatch() then return PaginatedBatch with default pagination values`() {

        val expected = PaginatedBatch<String>(
            key = "A",
            pageNumber = FLICKR_DEFAULT_PAGE,
            itemsPerPage = FLICKR_DEFAULT_ITEMS_PER_PAGE
        )

        val result = initialPaginatedBatch("A")

        assertEquals(expected, result)
    }
}
