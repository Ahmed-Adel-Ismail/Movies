package com.movies.core

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
}

internal object DataSources {
    internal var moviesMigrationDataSource = object : MigrationDataSource {}
    internal var moviesSearchDataSource = object : SearchDataSource {}
    internal var moviesDetailsDataSource = object : DetailsDataSource {}
}




