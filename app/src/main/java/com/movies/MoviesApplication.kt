package com.movies

import android.app.Application
import com.movies.core.integration.coreIntegration
import com.movies.data.DetailsDataSourceImplementer
import com.movies.data.MigrationDataSourceImplementer
import com.movies.data.SearchDataSourceImplementer
import com.movies.data.SearchResultDataSourceImplementer

class MoviesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        coreIntegration {
            with moviesMigrationDataSource MigrationDataSourceImplementer(applicationContext)
            with moviesSearchDataSource SearchDataSourceImplementer()
            with moviesDetailsDataSource DetailsDataSourceImplementer(applicationContext)
            with moviesSearchResultsDataSource SearchResultDataSourceImplementer(applicationContext)
            with moviesLogger MoviesLogger
        }
    }
}
