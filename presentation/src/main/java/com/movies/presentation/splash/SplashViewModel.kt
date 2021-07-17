package com.movies.presentation.splash

import androidx.lifecycle.ViewModel
import com.movies.core.migration.MigrationPort
import com.movies.core.migration.bindMigration
import com.movies.core.presentation.dispose
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class SplashViewModel : ViewModel(), MigrationPort {
    override val scheduler = Schedulers.io()
    override val migrationInProgress = BehaviorSubject.create<Boolean>()
    override val migrationFinished = BehaviorSubject.create<Boolean>()
    override val disposables = CompositeDisposable()
    override val errors = BehaviorSubject.create<Throwable>()
    override val bindMigration = bindMigration()
    override fun onCleared() = dispose()
}
