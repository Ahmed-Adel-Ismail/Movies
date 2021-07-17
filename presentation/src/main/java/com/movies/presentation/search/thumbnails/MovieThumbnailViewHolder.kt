package com.movies.presentation.search.thumbnails

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.movies.core.details.onSelectMovie
import com.movies.core.searching.results.ThumbnailPort
import com.movies.core.searching.results.onRequestImageUrl
import com.movies.presentation.R
import com.movies.presentation.navigate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Cancellable
import io.reactivex.rxkotlin.subscribeBy

const val ACTION_MOVIE_THUMBNAIL_CLICKED =
    "com.movies.presentation.search.thumbnails.ACTION_MOVIE_THUMBNAIL_CLICKED"

internal class MovieThumbnailViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
    View.inflate(parentView.context, R.layout.view_movie_thumbnail, null)
) {
    private val title: TextView by lazy { itemView.findViewById(R.id.movieThumbnailTitle) }
    private val progress: View by lazy { itemView.findViewById(R.id.movieThumbnailProgress) }
    private val image: ImageView by lazy { itemView.findViewById(R.id.movieThumbnail) }
    private var progressDisposable: Disposable? = null
    private var imageUrlCancellable: Cancellable? = null

    fun bind(item: ThumbnailPort) {
        unbind()
        val movie = item.movie.value ?: return
        title.text = movie.title
        progressDisposable = updateProgress(item)
        imageUrlCancellable = item.onRequestImageUrl {
            // image url received
        }

        itemView.setOnClickListener {
            item.onSelectMovie(movie)
            it.context.navigate(ACTION_MOVIE_THUMBNAIL_CLICKED)
        }
    }

    private fun updateProgress(item: ThumbnailPort) = item
        .loadingThumbnailImageUrl
        .share()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(item.errors::onNext) {
            progress.visibility = if (it) VISIBLE else GONE
        }

    fun unbind() {
        title.text = null
        itemView.setOnClickListener(null)
        imageUrlCancellable?.cancel()
        val lastProgressDisposable = progressDisposable
        if (lastProgressDisposable != null && !lastProgressDisposable.isDisposed) lastProgressDisposable.dispose()
    }
}
