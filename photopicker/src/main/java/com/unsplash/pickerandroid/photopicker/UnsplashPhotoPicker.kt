package com.unsplash.pickerandroid.photopicker

import android.app.Application

/**
 * Configuration singleton object.
 */
object UnsplashPhotoPicker {

    private lateinit var application: Application

    private lateinit var accessKey: String

    private lateinit var secretKey: String

    var isLoggingEnabled: Boolean = false

    fun init(
        application: Application,
        accessKey: String,
        secretKey: String,
        isLoggingEnabled: Boolean = false
    ): UnsplashPhotoPicker {
        this.application = application
        this.accessKey = accessKey
        this.secretKey = secretKey
        this.isLoggingEnabled = isLoggingEnabled
        return this
    }

    fun getApplication(): Application {
        return application
    }

    fun getAccessKey(): String {
        return accessKey
    }

    fun getSecretKey(): String {
        return secretKey
    }
}
