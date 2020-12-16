package com.diegor.redditreader

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RedditReaderApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Fresco.initialize(this)
    }
}