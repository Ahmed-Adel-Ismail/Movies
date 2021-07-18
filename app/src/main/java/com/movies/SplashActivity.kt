package com.movies

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.movies.presentation.splash.ACTION_SPLASH_COMPLETE
import com.movies.presentation.withNavigation

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        withNavigation { if (it == ACTION_SPLASH_COMPLETE) moveFromSplashToSearchScreen() }
    }

    private fun moveFromSplashToSearchScreen() {
        startActivity(Intent(this, SearchActivity::class.java))
        finish()
    }
}
