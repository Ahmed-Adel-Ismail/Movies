package com.movies.core.migration

import com.movies.core.entities.BusinessRule
import com.movies.core.presentation.PresentationPort
import io.reactivex.subjects.BehaviorSubject

interface MigrationPort : PresentationPort {
    val migrationInProgress: BehaviorSubject<Boolean>
    val migrationFinished: BehaviorSubject<Boolean>
    val bindMigration: BusinessRule
}
