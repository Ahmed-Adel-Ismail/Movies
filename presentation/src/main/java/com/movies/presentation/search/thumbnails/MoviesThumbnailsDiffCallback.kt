package com.movies.presentation.search.thumbnails

import androidx.recyclerview.widget.DiffUtil
import com.movies.core.searching.results.ThumbnailPort

internal class MoviesThumbnailsDiffCallback : DiffUtil.ItemCallback<ThumbnailPort>() {
    override fun areItemsTheSame(oldItem: ThumbnailPort, newItem: ThumbnailPort) =
        oldItem.movie.value?.title == newItem.movie.value?.title

    override fun areContentsTheSame(oldItem: ThumbnailPort, newItem: ThumbnailPort) =
        oldItem.movie.value?.rating == newItem.movie.value?.rating
}
