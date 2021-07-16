package com.movies

import android.app.Application
import com.movies.core.coreIntegration
import com.movies.core.MigrationDataSource
import com.movies.core.SearchDataSource

class MoviesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        coreIntegration {
            with moviesMigrationDataSource object : MigrationDataSource {}
            with moviesSearchDataSource object : SearchDataSource {}
        }
    }
}
