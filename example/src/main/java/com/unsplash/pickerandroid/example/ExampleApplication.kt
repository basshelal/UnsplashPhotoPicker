package com.unsplash.pickerandroid.example

import android.app.Application
import com.unsplash.pickerandroid.example.keys.UNSPLASH_ACCESS_KEY
import com.unsplash.pickerandroid.example.keys.UNSPLASH_SECRET_KEY
import com.unsplash.pickerandroid.photopicker.UnsplashPhotoPicker

class ExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // initializing the picker library
        UnsplashPhotoPicker.init(
            this,
            UNSPLASH_ACCESS_KEY,
            UNSPLASH_SECRET_KEY
            /* optional page size (number of photos per page) */
        ).setLoggingEnabled(true) // if you want to see the http requests */
    }
}
