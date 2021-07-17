package com.movies.presentation.details

import android.view.View
import android.widget.TextView
import com.movies.presentation.R
import com.movies.presentation.subscribeWithLifecycle

internal fun MovieDetailsFragment.bindViews(view: View) {
    bindTitle()
    bindYear(view)
    bindGenres(view)
    bindCast(view)
}

private fun MovieDetailsFragment.bindTitle() {
    viewModel.title.subscribeWithLifecycle(this) {
        activity?.title = it
    }
}

private fun MovieDetailsFragment.bindYear(view: View) {
    val year = view.findViewById<TextView>(R.id.movieDetailsYear)
    viewModel.year.subscribeWithLifecycle(this) {
        year.text = it
    }
}

private fun MovieDetailsFragment.bindCast(view: View) {
    val cast = view.findViewById<TextView>(R.id.movieDetailsCast)
    val castLabel = view.findViewById<TextView>(R.id.movieDetailsCastLabel)
    viewModel.genres.subscribeWithLifecycle(this) {
        if (it.isNotEmpty()) {
            showCast(cast, castLabel, it)
        } else {
            hideCast(cast, castLabel)
        }
    }
}

private fun hideCast(cast: TextView, castLabel: TextView) {
    cast.visibility = View.GONE
    castLabel.visibility = View.GONE
}

private fun showCast(
    cast: TextView,
    castLabel: TextView,
    it: List<String>
) {
    cast.visibility = View.VISIBLE
    castLabel.visibility = View.VISIBLE
    cast.text = it.joinToString()
}

private fun MovieDetailsFragment.bindGenres(view: View) {
    val genres = view.findViewById<TextView>(R.id.movieDetailsGenres)
    val genresLabel = view.findViewById<TextView>(R.id.movieDetailsGenresLabel)
    viewModel.genres.subscribeWithLifecycle(this) {
        if (it.isNotEmpty()) {
            showGenres(genres, genresLabel, it)
        } else {
            hideGenres(genres, genresLabel)
        }
    }
}

private fun hideGenres(genres: TextView, genresLabel: TextView) {
    genres.visibility = View.GONE
    genresLabel.visibility = View.GONE
}

private fun showGenres(
    genres: TextView,
    genresLabel: TextView,
    it: List<String>
) {
    genres.visibility = View.VISIBLE
    genresLabel.visibility = View.VISIBLE
    genres.text = it.joinToString()
}
