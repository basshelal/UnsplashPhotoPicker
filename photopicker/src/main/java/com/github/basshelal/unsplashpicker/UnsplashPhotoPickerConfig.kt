@file:Suppress("RedundantVisibilityModifier")

package com.github.basshelal.unsplashpicker

import android.app.Application
import com.github.basshelal.unsplashpicker.UnsplashPhotoPickerConfig.init

/**
 * Configuration for the UnsplashPhotoPicker library.
 *
 * Call [init] in your [Application] class to configure the library with your
 * [Application] instance, your Unsplash API Access Key and your Unsplash API Secret Key.
 *
 * See the [Unsplash Documentation](https://unsplash.com/documentation) for more details
 * on how to get started and get your access key and secret key.
 */
public object UnsplashPhotoPickerConfig {

    lateinit var application: Application
    lateinit var accessKey: String
    lateinit var secretKey: String
    var isLoggingEnabled: Boolean = false

    /**
     * Initializes the UnsplashPhotoPicker library.
     *
     * You must call this in your [Application] class to configure the library with your
     * [application] instance, your Unsplash API [accessKey] and your Unsplash API [secretKey].
     *
     * Remember to keep both of these secret, I recommend to create a `Keys.kt` file with
     * `const val`s for both and do not check the file into Version Control.
     *
     * Set [isLoggingEnabled] to `true` if you would like to have all HTTP requests logged,
     * this defaults to false. Warning, this will flood your logcat!
     *
     * See the [Unsplash Documentation](https://unsplash.com/documentation) for more details
     * on how to get started and get your access key and secret key.
     */
    public fun init(
        application: Application,
        accessKey: String,
        secretKey: String,
        isLoggingEnabled: Boolean = false
    ): UnsplashPhotoPickerConfig {
        this.application = application
        this.accessKey = accessKey
        this.secretKey = secretKey
        this.isLoggingEnabled = isLoggingEnabled
        return this
    }
}
