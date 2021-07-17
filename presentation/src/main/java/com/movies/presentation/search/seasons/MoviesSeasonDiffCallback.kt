package com.movies.presentation.search.seasons

import androidx.recyclerview.widget.DiffUtil

internal class MoviesSeasonDiffCallback : DiffUtil.ItemCallback<MoviesSeason>() {
    override fun areItemsTheSame(
        oldItem: MoviesSeason,
        newItem: MoviesSeason
    ) = oldItem.moviesSection.value?.year == newItem.moviesSection.value?.year

    override fun areContentsTheSame(
        oldItem: MoviesSeason,
        newItem: MoviesSeason
    ) = oldItem.moviesSection.value?.movies == newItem.moviesSection.value?.movies
}
