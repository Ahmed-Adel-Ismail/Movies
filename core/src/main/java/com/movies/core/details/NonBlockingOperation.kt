package com.movies.core.details

/**
 * indicates that an operation should not be blocking to the thread it is executed upon,
 * which means that it is not allowed to communicate with external data sources
 */
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
internal annotation class NonBlockingOperation
