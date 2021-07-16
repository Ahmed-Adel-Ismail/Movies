package com.movies.core

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

interface MigrationDataSource {
    fun isMigrationComplete(): Single<Boolean> = Single.error(NotImplementedError())
    fun startMigration(): Completable = Completable.error(NotImplementedError())
}

interface MigrationPort : PresentationPort {
    val migrationInProgress: BehaviorSubject<Boolean>
    val migrationFinished: BehaviorSubject<Boolean>
    val bindMigration: BusinessRule
}

@BusinessRules
fun MigrationPort.bindMigration() = withDisposable {
    DataSources
        .moviesMigrationDataSource
        .isMigrationComplete()
        .doOnSubscribe { migrationInProgress.onNext(true) }
        .subscribeOn(scheduler)
        .observeOn(scheduler)
        .filter { it }
        .switchIfEmpty(startMigration())
        .defaultIfEmpty(false)
        .doFinally { migrationInProgress.onNext(false) }
        .subscribeCatching { migrationFinished.onNext(true) }
}

private fun startMigration() = Maybe.defer {
    DataSources.moviesMigrationDataSource.startMigration().andThen(Maybe.just(true))
}


