package com.movies.core.migration

import com.movies.core.entities.BusinessRules
import com.movies.core.integration.DataSources
import com.movies.core.integration.Tracking
import com.movies.core.presentation.withDisposable
import io.reactivex.Maybe

@BusinessRules
fun MigrationPort.bindMigration() = withDisposable {
    DataSources.moviesMigrationDataSource
        .isMigrationComplete()
        .doOnSubscribe { migrationInProgress.onNext(true) }
        .subscribeOn(scheduler)
        .observeOn(scheduler)
        .onErrorReturn { false }
        .filter { it }
        .switchIfEmpty(startMigration())
        .defaultIfEmpty(false)
        .doFinally { migrationInProgress.onNext(false) }
        .subscribeCatching { migrationFinished.onNext(true) }
}

private fun startMigration() = Maybe.defer {
    Tracking.logger.logInfo("MigrationPort", "startMigration()")
    DataSources.moviesMigrationDataSource.startMigration().andThen(Maybe.just(true))
}


