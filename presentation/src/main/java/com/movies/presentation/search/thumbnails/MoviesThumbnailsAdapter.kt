package com.movies.presentation.search.thumbnails

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.movies.core.searching.results.ThumbnailsPort

internal class MoviesThumbnailsAdapter :
    ListAdapter<MovieThumbnails, MovieThumbnailViewHolder>(MoviesThumbnailsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MovieThumbnailViewHolder(parent)

    override fun onBindViewHolder(holder: MovieThumbnailViewHolder, position: Int) =
        holder.bind(currentList[position])

    override fun onViewRecycled(holder: MovieThumbnailViewHolder) {
        holder.unbind()
    }
}
