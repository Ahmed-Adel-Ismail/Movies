package com.movies

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.movies.presentation.search.thumbnails.ACTION_MOVIE_THUMBNAIL_CLICKED
import com.movies.presentation.withNavigation

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        withNavigation { if (it == ACTION_MOVIE_THUMBNAIL_CLICKED) openMovieDetails() }
    }

    private fun openMovieDetails() {
        startActivity(Intent(this, MovieDetailsActivity::class.java))
    }
}

