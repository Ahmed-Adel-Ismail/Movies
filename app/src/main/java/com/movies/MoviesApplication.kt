package com.movies

import android.app.Application
import com.movies.adapters.MigrationDataSourceImplementer
import com.movies.adapters.SearchDataSourceImplementer
import com.movies.core.details.DetailsDataSource
import com.movies.core.integration.coreIntegration
import com.movies.core.searching.results.SearchResultsDataSource

class MoviesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        coreIntegration {
            with moviesMigrationDataSource MigrationDataSourceImplementer(applicationContext)
            with moviesSearchDataSource SearchDataSourceImplementer()
            with moviesDetailsDataSource object : DetailsDataSource {}
            with moviesSearchResultsDataSource object : SearchResultsDataSource {}
            with moviesLogger MoviesLogger
        }
    }
}
