package com.movies

import android.util.Log
import com.movies.core.tracking.Logger

object MoviesLogger : Logger {
    override fun logInfo(tag: String, message: String?) {
        if (BuildConfig.DEBUG) Log.i(tag, message ?: "")
    }

    override fun logError(tag: String, message: String?) {
        if (BuildConfig.DEBUG) Log.e(tag, message ?: "")
    }

    override fun logException(throwable: Throwable) {
        if (BuildConfig.DEBUG) throwable.printStackTrace()
    }
}
