package com.movies.presentation.search.seasons

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter

internal class MoviesSeasonAdapter :
    ListAdapter<MoviesSeason, MoviesSeasonViewHolder>(MoviesSeasonDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MoviesSeasonViewHolder(parent)

    override fun onBindViewHolder(holder: MoviesSeasonViewHolder, position: Int) =
        holder.bind(currentList[position])

    override fun onViewRecycled(holder: MoviesSeasonViewHolder) {
        holder.unbind()
    }
}
