package com.movies.adapters

import org.junit.Assert.assertEquals
import org.junit.Test

class MigrationDataSourceImplementerKtTest {

    @Test
    fun `toMoviesSections() then return sections sorted descending by year`() {
        val expected = listOf("3", "2", "1")
        val result = MoviesDataSet().toMoviesSections()
            .map { it.year }

        assertEquals(expected, result)
    }

    @Test
    fun `toMoviesSections() then return movies grouped by year`() {

        val expected = listOf("3" to 3, "2" to 3, "1" to 4)
        val result = MoviesDataSet().toMoviesSections()
            .map { it.year to it.movies?.size }

        assertEquals(expected, result)
    }

    @Test
    fun `toMoviesSections() then return movies ordered descending by rating`() {
        val expected = listOf(
            "3" to listOf(9, 6, 3),
            "2" to listOf(8, 5, 2),
            "1" to listOf(10, 7, 4, 1)
        )
        val result = MoviesDataSet().toMoviesSections()
            .map { section -> section.year to section.movies?.map { it.rating } }

        assertEquals(expected, result)
    }

    @Test
    fun `toMoviesSections() then return valid MoviesSections`() {
        assertEquals(MoviesSectionsDataSet(), MoviesDataSet().toMoviesSections())
    }
}
