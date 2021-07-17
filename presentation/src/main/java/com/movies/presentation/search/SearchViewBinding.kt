package com.movies.presentation.search

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.movies.presentation.R
import com.movies.presentation.search.seasons.MoviesSeasonAdapter
import com.movies.presentation.subscribeWithLifecycle

internal fun SearchFragment.bindViews(view: View) {

    val progressBar = view.findViewById<View>(R.id.searchProgressBar)
    viewModel.loadingPagedItems.subscribeWithLifecycle(this) {
        progressBar.visibility = if (it) View.VISIBLE else View.GONE
    }

    val adapter = initializeAdapter(view)
    viewModel.searchResults.subscribeWithLifecycle(this) {
        adapter.submitList(it)
    }

    viewModel.errors.subscribeWithLifecycle(this) {
        Toast.makeText(activity, it.message ?: it.toString(), Toast.LENGTH_SHORT).show()
    }
}

private fun initializeAdapter(view: View): MoviesSeasonAdapter {
    val adapter = MoviesSeasonAdapter()
    val recyclerView = view.findViewById<RecyclerView>(R.id.searchRecyclerView)
    recyclerView.adapter = adapter
    return adapter
}
