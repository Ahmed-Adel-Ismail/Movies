package com.movies.adapters

import com.movies.core.entities.Movie
import com.movies.core.entities.MoviesSection
import com.movies.core.entities.PaginatedBatch
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchDataSourceImplementerTest {
    @Test
    fun `searchMovies() then return sections with movies title matching search term`() {

        val expected = listOf(
            MoviesSection(
                year = "3", movies = listOf(
                    Movie(title = "Mission Impossible 3", year = "3", rating = 3)
                )
            ),
            MoviesSection(
                year = "2", movies = listOf(
                    Movie(title = "Mission Impossible 2", year = "2", rating = 2)
                )
            ),
            MoviesSection(
                year = "1", movies = listOf(
                    Movie(title = "Mission Impossible 4", year = "1", rating = 4),
                )
            )
        )

        InMemoryCache.moviesSections = MoviesSectionsDataSet()
        val dataSource = SearchDataSourceImplementer()

        dataSource.searchMovies(PaginatedBatch("mis"))
            .map { it.items }
            .test()
            .assertValue(expected)
    }

    @Test
    fun `filteredMoviesSections() then return sections with movies title matching search term`() {

        val expected = listOf(
            MoviesSection(
                year = "3", movies = listOf(
                    Movie(title = "Mission Impossible 3", year = "3", rating = 3)
                )
            ),
            MoviesSection(
                year = "2", movies = listOf(
                    Movie(title = "Mission Impossible 2", year = "2", rating = 2)
                )
            ),
            MoviesSection(
                year = "1", movies = listOf(
                    Movie(title = "Mission Impossible 4", year = "1", rating = 4),
                )
            )
        )
        val result = filteredMoviesSections("mis", MoviesSectionsDataSet(), 10)

        assertEquals(expected, result)
    }

    @Test
    fun `filteredMoviesSections() with empty searchTerm then return all movies`() {

        val result = filteredMoviesSections("", MoviesSectionsDataSet(), 100)

        assertEquals(MoviesSectionsDataSet(), result)
    }

    @Test
    fun `filteredMoviesSections() then return sections with top movies only`() {

        val expected = listOf(
            MoviesSection(
                year = "3", movies = listOf(Movie(title = "Fast 9", year = "3", rating = 9))
            ),
            MoviesSection(
                year = "2", movies = listOf(Movie(title = "Kill Bill 2", year = "2", rating = 8))
            ),
            MoviesSection(
                year = "1", movies = listOf(Movie(title = "Terminator", year = "1", rating = 10))
            )
        )

        val result = filteredMoviesSections("", MoviesSectionsDataSet(), 1)

        assertEquals(expected, result)
    }

    @Test
    fun `filteredMoviesSections() then never return sections with empty movies`() {

        val expected = listOf(
            MoviesSection(
                year = "2", movies = listOf(Movie(title = "Kill Bill 2", year = "2", rating = 8))
            )
        )

        val result = filteredMoviesSections("Kill Bill 2", MoviesSectionsDataSet(), 10)

        assertEquals(expected, result)
    }

    @Test
    fun `searchMovies() with null searchTerm then return all moviesSections`() {
        InMemoryCache.moviesSections = MoviesSectionsDataSet()
        val dataSource = SearchDataSourceImplementer()

        dataSource.searchMovies(PaginatedBatch(null))
            .map { it.items }
            .test()
            .assertValue(MoviesSectionsDataSet())
    }

    @Test
    fun `searchMovies() with empty searchTerm then return all moviesSections`() {
        InMemoryCache.moviesSections = MoviesSectionsDataSet()
        val dataSource = SearchDataSourceImplementer()

        dataSource.searchMovies(PaginatedBatch(""))
            .map { it.items }
            .test()
            .assertValue(MoviesSectionsDataSet())
    }

    @After
    fun tearDown() {
        InMemoryCache.moviesSections = listOf()
    }
}
