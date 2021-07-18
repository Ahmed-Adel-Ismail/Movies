package com.movies.data.flickr

import org.junit.Assert.assertEquals
import org.junit.Test

class FlickrApiServiceKtTest {

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
