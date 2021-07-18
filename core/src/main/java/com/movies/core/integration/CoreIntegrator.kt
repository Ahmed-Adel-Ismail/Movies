package com.movies.core.integration

import com.movies.core.details.DetailsDataSource
import com.movies.core.migration.MigrationDataSource
import com.movies.core.searching.SearchDataSource
import com.movies.core.searching.results.SearchResultsDataSource
import com.movies.core.tracking.Logger

@DslMarker
private annotation class CoreIntegration

@CoreIntegration
fun coreIntegration(integrator: CoreIntegrator.() -> Unit) {
    CoreIntegrator.apply(integrator)
}

object CoreIntegrator {
    @CoreIntegration
    val with = this

    @CoreIntegration
    infix fun moviesMigrationDataSource(dataSource: MigrationDataSource) {
        DataSources.moviesMigrationDataSource = dataSource
    }

    @CoreIntegration
    infix fun moviesSearchDataSource(dataSource: SearchDataSource) {
        DataSources.moviesSearchDataSource = dataSource
    }

    @CoreIntegration
    infix fun moviesDetailsDataSource(dataSource: DetailsDataSource) {
        DataSources.moviesDetailsDataSource = dataSource
    }

    @CoreIntegration
    infix fun moviesSearchResultsDataSource(dataSource: SearchResultsDataSource) {
        DataSources.moviesSearchResultsDataSource = dataSource
    }

    @CoreIntegration
    infix fun moviesLogger(logger: Logger) {
        Tracking.logger = logger
    }
}
