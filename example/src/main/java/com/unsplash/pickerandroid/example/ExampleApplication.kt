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
            UNSPLASH_ACCESS_KEY, // Your access key, remember this must be secret!
            UNSPLASH_SECRET_KEY, // Your secret key, remember this must be secret!
            false // if you want to see the http requests */
        )
    }
}
