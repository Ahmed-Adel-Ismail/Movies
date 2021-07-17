@file:Suppress("TestFunctionName")

package com.movies.adapters

import com.movies.core.entities.Movie
import com.movies.core.entities.MoviesSection

fun MoviesDataSet() = listOf(
    Movie(title = "Titanic", year = "1", rating = 1),
    Movie(title = "Mission Impossible 2", year = "2", rating = 2),
    Movie(title = "Mission Impossible 3", year = "3", rating = 3),
    Movie(title = "Mission Impossible 4", year = "1", rating = 4),
    Movie(title = "Fast And Furious", year = "2", rating = 5),
    Movie(title = "Fast And Furious 2", year = "3", rating = 6),
    Movie(title = "Kill Bill", year = "1", rating = 7),
    Movie(title = "Kill Bill 2", year = "2", rating = 8),
    Movie(title = "Fast 9", year = "3", rating = 9),
    Movie(title = "Terminator", year = "1", rating = 10)
)

fun MoviesSectionsDataSet() = listOf(
    MoviesSection(
        year = "3", movies = listOf(
            Movie(title = "Fast 9", year = "3", rating = 9),
            Movie(title = "Fast And Furious 2", year = "3", rating = 6),
            Movie(title = "Mission Impossible 3", year = "3", rating = 3)
        )
    ),
    MoviesSection(
        year = "2", movies = listOf(
            Movie(title = "Kill Bill 2", year = "2", rating = 8),
            Movie(title = "Fast And Furious", year = "2", rating = 5),
            Movie(title = "Mission Impossible 2", year = "2", rating = 2)
        )
    ),
    MoviesSection(
        year = "1", movies = listOf(
            Movie(title = "Terminator", year = "1", rating = 10),
            Movie(title = "Kill Bill", year = "1", rating = 7),
            Movie(title = "Mission Impossible 4", year = "1", rating = 4),
            Movie(title = "Titanic", year = "1", rating = 1),
        )
    )
)
