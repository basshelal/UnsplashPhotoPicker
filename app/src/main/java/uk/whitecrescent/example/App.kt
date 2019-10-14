package uk.whitecrescent.example

import android.app.Application
import com.github.basshelal.unsplashpicker.UnsplashPhotoPickerConfig

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        UnsplashPhotoPickerConfig.init(
            application = this,
            accessKey = ACCESS_KEY,
            secretKey = SECRET_KEY,
            unsplashAppName = APP_NAME,
            isLoggingEnabled = true
        )
    }
}