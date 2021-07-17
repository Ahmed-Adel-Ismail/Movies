package com.movies.presentation.splash

import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.movies.presentation.R
import com.movies.presentation.navigate
import com.movies.presentation.subscribeWithLifecycle

const val ACTION_SPLASH_COMPLETE = "com.movies.presentation.splash.ACTION_SPLASH_COMPLETE"

internal fun SplashFragment.bindViews(view: View) {
    val progress = view.findViewById<ProgressBar>(R.id.splashProgressBar)
    viewModel.migrationInProgress.subscribeWithLifecycle(this) {
        progress?.visibility = if (it) View.VISIBLE else View.GONE
    }

    viewModel.migrationFinished.subscribeWithLifecycle(this) {
        activity?.navigate(ACTION_SPLASH_COMPLETE)
    }

    viewModel.errors.subscribeWithLifecycle(this) {
        it.printStackTrace()
        Toast.makeText(activity, it.message ?: it.toString(), Toast.LENGTH_LONG).show()
    }
}
