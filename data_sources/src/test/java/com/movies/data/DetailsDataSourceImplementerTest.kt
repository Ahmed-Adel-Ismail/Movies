package com.movies.data

import com.movies.core.entities.NoMoreResultsException
import com.movies.core.entities.PaginatedBatch
import com.movies.core.pagination.plus
import com.movies.data.flickr.FLICKR_DEFAULT_ITEMS_PER_PAGE
import com.movies.data.flickr.FLICKR_DEFAULT_PAGE
import com.movies.data.flickr.FlickrApiService
import com.movies.data.flickr.FlickrGateway
import com.movies.data.flickr.FlickrPicture
import com.movies.data.flickr.FlickrPicturesMetadata
import com.movies.data.flickr.FlickrSearchRequest
import com.movies.data.flickr.FlickrSearchResult
import com.movies.data.flickr.imageUrl
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock

class DetailsDataSourceImplementerTest {

    @Test
    fun `requestMovieImagesBatch() then invoke FlickrGateway_requestPaginatedBatch() with title`() {
        runBlocking {

            var result: String? = null

            val gateway = object : FlickrGateway {
                override val service = mock<FlickrApiService>()
                override val apiKey = "1"

                override suspend fun requestPictures(searchRequest: FlickrSearchRequest): FlickrSearchResult {
                    result = searchRequest.title
                    return FlickrSearchResult(
                        FlickrPicturesMetadata(
                            listOf(FlickrPicture(), FlickrPicture())
                        )
                    )
                }
            }

            val dataSource = DetailsDataSourceImplementer(mock(), gateway)

            dataSource.requestMovieImagesBatch(PaginatedBatch("A", pageNumber = 1)).blockingGet()

            assertEquals("A", result)
        }
    }

    @Test
    fun `requestMovieImagesBatch() then invoke FlickrGateway_requestPaginatedBatch() with pageNumber`() {
        var result: Int? = null

        val gateway = object : FlickrGateway {
            override val service = mock<FlickrApiService>()
            override val apiKey = "1"

            override suspend fun requestPictures(searchRequest: FlickrSearchRequest): FlickrSearchResult {
                result = searchRequest.page
                return FlickrSearchResult(
                    FlickrPicturesMetadata(
                        listOf(FlickrPicture(), FlickrPicture())
                    )
                )
            }
        }

        val dataSource = DetailsDataSourceImplementer(mock(), gateway)

        dataSource.requestMovieImagesBatch(PaginatedBatch("A", pageNumber = 10)).blockingGet()

        assertEquals(10, result)
    }

    @Test
    fun `requestMovieImagesBatch() with null pageNumber then pass FLICKR_DEFAULT_PAGE`() {
        var result: Int? = null

        val gateway = object : FlickrGateway {
            override val service = mock<FlickrApiService>()
            override val apiKey = "1"

            override suspend fun requestPictures(searchRequest: FlickrSearchRequest): FlickrSearchResult {
                result = searchRequest.page
                return FlickrSearchResult(
                    FlickrPicturesMetadata(
                        listOf(FlickrPicture(), FlickrPicture())
                    )
                )
            }
        }

        val dataSource = DetailsDataSourceImplementer(mock(), gateway)

        dataSource.requestMovieImagesBatch(PaginatedBatch("A", pageNumber = null)).blockingGet()

        assertEquals(FLICKR_DEFAULT_PAGE, result)
    }

    @Test
    fun `requestMovieImagesBatch() with null itemsPerPage then pass FLICKR_DEFAULT_ITEMS_PER_PAGE`() {
        var result: Int? = null

        val gateway = object : FlickrGateway {
            override val service = mock<FlickrApiService>()
            override val apiKey = "1"

            override suspend fun requestPictures(searchRequest: FlickrSearchRequest): FlickrSearchResult {
                result = searchRequest.itemsPerPage
                return FlickrSearchResult(
                    FlickrPicturesMetadata(
                        listOf(FlickrPicture(), FlickrPicture())
                    )
                )
            }
        }

        val dataSource = DetailsDataSourceImplementer(mock(), gateway)

        dataSource.requestMovieImagesBatch(PaginatedBatch("A", pageNumber = null)).blockingGet()

        assertEquals(FLICKR_DEFAULT_ITEMS_PER_PAGE, result)
    }

    @Test
    fun `requestMovieImagesBatch() successfully then invoke and return PaginatedBatch_plus(List)`() {

        val requestBatch = PaginatedBatch("A", pageNumber = 10, items = listOf("1"))

        val gateway = object : FlickrGateway {
            override val service = mock<FlickrApiService>()
            override val apiKey = "1"

            override suspend fun requestPictures(searchRequest: FlickrSearchRequest): FlickrSearchResult {
                return FlickrSearchResult(
                    FlickrPicturesMetadata(
                        listOf(FlickrPicture(), FlickrPicture())
                    )
                )
            }
        }

        val dataSource = DetailsDataSourceImplementer(mock(), gateway)

        val result = dataSource.requestMovieImagesBatch(requestBatch).blockingGet()

        assertEquals(
            requestBatch + listOf(FlickrPicture().imageUrl(), FlickrPicture().imageUrl()),
            result
        )
    }

    @Test
    fun `requestMovieImagesBatch() failure then invoke and return PaginatedBatch_plus(Throwable)`() {
        val requestBatch = PaginatedBatch("A", pageNumber = 10, items = listOf("1"))

        val gateway = object : FlickrGateway {
            override val service = mock<FlickrApiService>()
            override val apiKey = "1"

            override suspend fun requestPictures(searchRequest: FlickrSearchRequest): FlickrSearchResult {
                return FlickrSearchResult(
                    FlickrPicturesMetadata(
                        listOf()
                    )
                )
            }
        }

        val dataSource = DetailsDataSourceImplementer(mock(), gateway)

        val result = dataSource.requestMovieImagesBatch(requestBatch).blockingGet()

        assertEquals(requestBatch + NoMoreResultsException, result)
    }
}
