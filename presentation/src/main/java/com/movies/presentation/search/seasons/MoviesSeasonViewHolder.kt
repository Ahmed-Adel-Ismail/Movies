package com.movies.presentation.search.seasons

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.movies.presentation.R
import com.movies.presentation.search.thumbnails.MoviesThumbnailsAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

internal class MoviesSeasonViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
    View.inflate(parentView.context, R.layout.view_movies_section, null)
) {
    private val year: TextView by lazy { itemView.findViewById(R.id.yearTextView) }
    private val moviesRecyclerView: RecyclerView by lazy { itemView.findViewById(R.id.moviesRecyclerView) }
    private var thumbnailsDisposable: Disposable? = null

    fun bind(item: MoviesSeason) {
        unbind()
        initializeThumbnailsAdapter()
        year.text = item.moviesSection.value?.year
        thumbnailsDisposable = updateThumbnails(item)
    }

    private fun initializeThumbnailsAdapter() {
        if (moviesRecyclerView.adapter == null) {
            moviesRecyclerView.adapter = MoviesThumbnailsAdapter()
        }
    }

    private fun updateThumbnails(item: MoviesSeason) = item.moviesThumbnails
        .share()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(item::updateErrors) {
            val adapter = moviesRecyclerView.adapter as MoviesThumbnailsAdapter
            adapter.submitList(it)
        }

    fun unbind() {
        val lastDisposable = thumbnailsDisposable ?: return
        if (!lastDisposable.isDisposed) lastDisposable.dispose()
    }
}


