package com.movies.presentation.details

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.movies.presentation.R
import com.movies.presentation.subscribeWithLifecycle

internal fun MovieDetailsFragment.bindViews(view: View) {
    bindRecyclerView(view)
    bindProgress(view)
    bindMovieDetails(view)
}

private fun MovieDetailsFragment.bindRecyclerView(view: View) {
    val adapter = MovieDetailsThumbnailsAdapter()
    val recycler = view.findViewById<RecyclerView>(R.id.detailsRecyclerView)
    recycler.adapter = adapter
    viewModel.pagedItemsResult.subscribeWithLifecycle(this) {
        if (it.error == null) adapter.submitList(it.items ?: listOf())
    }
}

private fun MovieDetailsFragment.bindProgress(view: View) {
    val progress = view.findViewById<View>(R.id.detailsProgressBar)
    viewModel.loadingPagedItems.subscribeWithLifecycle(this) {
        progress.visibility = if (it) VISIBLE else GONE
    }
}

private fun MovieDetailsFragment.bindMovieDetails(view: View) {
    val year = view.findViewById<TextView>(R.id.movieDetailsYear)

    val cast = view.findViewById<TextView>(R.id.movieDetailsCast)
    val castLabel = view.findViewById<TextView>(R.id.movieDetailsCastLabel)

    val genres = view.findViewById<TextView>(R.id.movieDetailsGenres)
    val genresLabel = view.findViewById<TextView>(R.id.movieDetailsGenresLabel)

    viewModel.movie.subscribeWithLifecycle(this) {

        activity?.title = it.title

        year.text = it.year

        if (it.cast?.isNotEmpty() == true) show(cast, castLabel, it.cast)
        else hide(cast, castLabel)

        if (it.genres?.isNotEmpty() == true) show(genres, genresLabel, it.genres)
        else hide(genres, genresLabel)

    }
}

private fun hide(textView: TextView, labelsView: TextView) {
    textView.visibility = View.GONE
    labelsView.visibility = View.GONE
}

private fun show(
    textView: TextView,
    labelView: TextView,
    values: List<String>?
) {
    textView.visibility = View.VISIBLE
    labelView.visibility = View.VISIBLE
    textView.text = values?.joinToString()
}
