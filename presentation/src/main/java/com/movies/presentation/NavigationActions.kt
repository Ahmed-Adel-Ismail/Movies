package com.movies.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.movies.core.integration.Tracking

@DslMarker
private annotation class NavigationDsl

private const val ACTION_NAVIGATE = "com.movies.presentation.ACTION_NAVIGATE"
private const val ACTION_KEY = "com.movies.presentation.ACTION_KEY"

@NavigationDsl
fun Context.navigate(action: String) {
    val intent = Intent(ACTION_NAVIGATE)
    intent.putExtra(ACTION_KEY, action)
    sendBroadcast(intent)
}

@NavigationDsl
fun AppCompatActivity.withNavigation(onNavigationAction: (String?) -> Unit) {
    val navigator = navigatorReceiver(onNavigationAction)

    lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun onCreate() {
            runCatching {
                registerReceiver(navigator, IntentFilter(ACTION_NAVIGATE))
            }.onFailure {
                Tracking.logger.logException(it)
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            runCatching {
                unregisterReceiver(navigator)
            }.onFailure {
                Tracking.logger.logException(it)
            }
        }
    })
}

private fun navigatorReceiver(onNavigationAction: (String?) -> Unit) =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onNavigationAction(intent?.getStringExtra(ACTION_KEY))
        }
    }


