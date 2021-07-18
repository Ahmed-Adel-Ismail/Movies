package com.movies.presentation.details

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.movies.presentation.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val IMAGE_DELAY_MILLIS = 1000L

class MovieDetailsThumbnailViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
    View.inflate(parentView.context, R.layout.view_movie_details_thumbnail, null)
) {
    private val progress by lazy { itemView.findViewById<View>(R.id.movieDetailsThumbnailProgress) }
    private val image: ImageView by lazy { itemView.findViewById(R.id.movieDetailsThumbnailView) }
    private var imageLoadingJob: Job? = null

    fun bind(url: String) {
        unbind()
        imageLoadingJob = image.context.let { it as? LifecycleOwner }?.lifecycleScope?.launch {
            delay(IMAGE_DELAY_MILLIS)
            progress.visibility = GONE
            image.visibility = VISIBLE
            Glide.with(image.context)
                .load(url)
                .placeholder(android.R.drawable.ic_menu_crop)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(android.R.drawable.ic_dialog_alert)
                .into(image)
        }
    }

    fun unbind() {
        progress.visibility = VISIBLE
        image.visibility = View.INVISIBLE
        image.setImageResource(0)
        image.setImageDrawable(null)
        image.setImageBitmap(null)
        imageLoadingJob?.cancel()
    }
}
