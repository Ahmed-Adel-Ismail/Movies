package com.movies.presentation.search.thumbnails

import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.movies.core.searching.results.ThumbnailsPort
import com.movies.core.searching.results.onRequestImageUrl
import com.movies.core.searching.results.onSelectMovie
import com.movies.presentation.R
import com.movies.presentation.navigate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Cancellable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val ACTION_MOVIE_THUMBNAIL_CLICKED =
    "com.movies.presentation.search.thumbnails.ACTION_MOVIE_THUMBNAIL_CLICKED"

private const val IMAGE_DELAY_MILLIS = 500L

internal class MovieThumbnailViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
    View.inflate(parentView.context, R.layout.view_movie_thumbnail, null)
) {
    private val title: TextView by lazy { itemView.findViewById(R.id.movieThumbnailTitle) }
    private val progress: View by lazy { itemView.findViewById(R.id.movieThumbnailProgress) }
    private val image: ImageView by lazy { itemView.findViewById(R.id.movieThumbnail) }
    private var progressDisposable: Disposable? = null
    private var imageUrlCancellable: Cancellable? = null
    private var imageLoadingJob: Job? = null

    fun bind(item: MovieThumbnails) {
        unbind()
        val movie = item.movie.value ?: return
        title.text = movie.title
        progressDisposable = updateProgress(item)
        imageLoadingJob = loadImage(item)
        itemView.setOnClickListener {
            item.onSelectMovie()
            it.context.navigate(ACTION_MOVIE_THUMBNAIL_CLICKED)
        }
    }

    private fun loadImage(item: MovieThumbnails) = image.context
        .let { it as? LifecycleOwner }
        ?.lifecycleScope
        ?.launch {
            delay(IMAGE_DELAY_MILLIS)
            imageUrlCancellable = loadUrlThenImages(item)
        }

    private fun loadUrlThenImages(item: MovieThumbnails) =
        item.onRequestImageUrl(AndroidSchedulers.mainThread()) {
            image.visibility = VISIBLE
            Glide.with(image.context)
                .load(it.firstOrNull())
                .placeholder(android.R.drawable.ic_menu_crop)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(android.R.drawable.ic_dialog_alert)
                .fitCenter()
                .into(image)
                .clearOnDetach()
        }

    private fun updateProgress(item: ThumbnailsPort) = item
        .loadingPagedItems
        .share()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(item::updateErrors) {
            progress.visibility = if (it) VISIBLE else GONE
        }

    fun unbind() {
        image.visibility = INVISIBLE
        image.setImageResource(0)
        image.setImageDrawable(null)
        image.setImageBitmap(null)
        imageLoadingJob?.cancel()
        title.text = null
        itemView.setOnClickListener(null)
        imageUrlCancellable?.cancel()
        val lastProgressDisposable = progressDisposable
        if (lastProgressDisposable != null && !lastProgressDisposable.isDisposed) lastProgressDisposable.dispose()
    }
}
