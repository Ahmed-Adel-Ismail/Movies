package com.movies.data

import com.movies.core.entities.Movie
import com.movies.core.entities.MoviesSection
import com.movies.core.entities.PaginatedBatch
import com.movies.core.searching.SearchDataSource
import io.reactivex.Single

internal const val TOP_MOVIES_COUNT = 5

class SearchDataSourceImplementer : SearchDataSource {
    override fun searchMovies(batch: PaginatedBatch<MoviesSection>) = Single.fromCallable {
        val searchTerm = batch.key
        if (searchTerm.isNullOrBlank()) batch.copy(items = InMemoryCache.moviesSections.toList())
        else batch.copy(items = filteredMoviesSections(searchTerm))
    }
}

internal fun filteredMoviesSections(
    searchTerm: String,
    moviesSections: List<MoviesSection> = InMemoryCache.moviesSections,
    topCount: Int = TOP_MOVIES_COUNT
) = moviesSections.map { MoviesSection(it.year, matchingMovies(searchTerm, it, topCount)) }
    .filter { it.movies?.isNotEmpty() == true }

private fun matchingMovies(
    searchTerm: String,
    section: MoviesSection,
    topCount: Int
): List<Movie> = section.movies
    ?.filter { it.title?.contains(searchTerm, true) == true }
    ?.take(topCount) ?: listOf()


