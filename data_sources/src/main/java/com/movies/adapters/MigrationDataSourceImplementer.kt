package com.movies.adapters

import android.content.Context
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.movies.core.migration.MigrationDataSource
import com.movies.core.entities.Movie
import com.movies.core.entities.MoviesSection
import io.reactivex.Completable
import io.reactivex.Single
import java.io.InputStreamReader

class MigrationDataSourceImplementer(
    private val context: Context,
    private val gson: Gson = Gson()
) : MigrationDataSource {

    override fun isMigrationComplete() = Single.fromCallable {
        InMemoryCache.moviesSections.isNotEmpty()
    }

    override fun startMigration() = Completable.fromAction {
        context.resources.openRawResource(R.raw.movies).use { inputStream ->
            InputStreamReader(inputStream, "UTF-8").use { inputStreamReader ->
                JsonReader(inputStreamReader).use { reader ->
                    InMemoryCache.moviesSections = reader.readJsonMoviesAsSections()
                }
            }
        }
    }

    private fun JsonReader.readJsonMoviesAsSections(): List<MoviesSection> {
        val movies = mutableListOf<Movie>()
        beginArray()
        while (hasNext()) movies.add(gson.fromJson(this, Movie::class.java))
        endArray()
        return movies.toMoviesSections()
    }
}

object NoMoviesFoundException : RuntimeException()

internal fun List<Movie>.toMoviesSections() = groupBy { it.year }
    .map { entry -> MoviesSection(entry.key, entry.value.sortedByDescending { it.rating }) }
    .sortedByDescending { it.year }
    .takeUnless { it.isEmpty() }
    ?: throw NoMoviesFoundException


