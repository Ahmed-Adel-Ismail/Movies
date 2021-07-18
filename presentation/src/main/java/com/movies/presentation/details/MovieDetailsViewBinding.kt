package com.movies.presentation.details

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.movies.core.details.onLoadMoreImages
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
    recycler.addOnScrollListener(onScrollListener(recycler))
    viewModel.pagedItemsResult.subscribeWithLifecycle(this) {
        if (it.error == null) adapter.submitList(it.items ?: listOf())
    }
}

private fun MovieDetailsFragment.onScrollListener(recycler: RecyclerView) =
    object : OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, directionX: Int, directionY: Int) {
            if (directionY <= 0) return
            val layoutManager = recycler.layoutManager as? GridLayoutManager ?: return
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            if (isLoadingRequired(visibleItemCount, firstVisibleItemPosition, totalItemCount)) {
                viewModel.onLoadMoreImages()
            }
        }
    }

private fun isLoadingRequired(
    visibleItemCount: Int,
    firstVisibleItemPosition: Int,
    totalItemCount: Int
) = firstVisibleItemPosition >= 0 &&
    ((visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - 10))

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

        if (it.cast?.isNotEmpty() == true) showAsCommaSeparated(cast, castLabel, it.cast)
        else hide(cast, castLabel)

        if (it.genres?.isNotEmpty() == true) showAsCommaSeparated(genres, genresLabel, it.genres)
        else hide(genres, genresLabel)

    }
}

private fun hide(textView: TextView, labelsView: TextView) {
    textView.visibility = GONE
    labelsView.visibility = GONE
}

private fun showAsCommaSeparated(
    textView: TextView,
    labelView: TextView,
    values: List<String>?
) {
    textView.visibility = VISIBLE
    labelView.visibility = VISIBLE
    textView.text = values?.joinToString()
}
