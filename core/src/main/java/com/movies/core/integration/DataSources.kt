package com.movies.core.integration

import com.movies.core.details.DetailsDataSource
import com.movies.core.migration.MigrationDataSource
import com.movies.core.searching.SearchDataSource
import com.movies.core.searching.results.SearchResultsDataSource

internal object DataSources {
    internal var moviesMigrationDataSource = object : MigrationDataSource {}
    internal var moviesSearchDataSource = object : SearchDataSource {}
    internal var moviesDetailsDataSource = object : DetailsDataSource {}
    internal var moviesSearchResultsDataSource = object : SearchResultsDataSource {}
}
