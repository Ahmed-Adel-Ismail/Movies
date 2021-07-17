package com.movies.presentation.search.thumbnails

import androidx.recyclerview.widget.DiffUtil
import com.movies.core.searching.results.ThumbnailsPort

internal class MoviesThumbnailsDiffCallback : DiffUtil.ItemCallback<MovieThumbnails>() {
    override fun areItemsTheSame(oldItem: MovieThumbnails, newItem: MovieThumbnails) =
        oldItem.movie.value?.title == newItem.movie.value?.title

    override fun areContentsTheSame(oldItem: MovieThumbnails, newItem: MovieThumbnails) =
        oldItem.movie.value?.rating == newItem.movie.value?.rating
}
