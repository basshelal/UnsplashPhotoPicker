package com.unsplash.pickerandroid.example

import android.app.Application
import com.unsplash.pickerandroid.photopicker.UnsplashPhotoPicker

class ExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // initializing the picker library
        UnsplashPhotoPicker.init(
            this,
            "UNSPLASH_ACCESS_KEY", // Your access key, remember these must be secret!
            "UNSPLASH_SECRET_KEY", // Your secret key, remember these must be secret!
            false // if you want to see the http requests */
        )
    }
}
