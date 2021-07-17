package com.movies.core.migration

import io.reactivex.Completable
import io.reactivex.Single

interface MigrationDataSource {
    fun isMigrationComplete(): Single<Boolean> = Single.error(NotImplementedError())
    fun startMigration(): Completable = Completable.error(NotImplementedError())
}
