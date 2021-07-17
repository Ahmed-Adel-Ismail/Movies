package com.movies

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.movies.presentation.search.thumbnails.ACTION_MOVIE_THUMBNAIL_CLICKED
import com.movies.presentation.withNavigation

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        withNavigation {
            when (it) {
                ACTION_MOVIE_THUMBNAIL_CLICKED -> openMovieDetails()
            }
        }
    }
}

private fun SearchActivity.openMovieDetails() {
    Toast.makeText(this, "thumbnail clicked", Toast.LENGTH_SHORT).show()
}
