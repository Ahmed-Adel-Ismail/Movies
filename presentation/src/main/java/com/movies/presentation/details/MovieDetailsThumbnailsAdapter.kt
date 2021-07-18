package com.movies.presentation.details

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter

internal class MovieDetailsThumbnailsAdapter :
    ListAdapter<String, MovieDetailsThumbnailViewHolder>(MovieDetailsThumbnailDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MovieDetailsThumbnailViewHolder(parent)

    override fun onBindViewHolder(holder: MovieDetailsThumbnailViewHolder, position: Int) =
        holder.bind(currentList[position])

    override fun onViewRecycled(holder: MovieDetailsThumbnailViewHolder) {
        holder.unbind()
    }
}

