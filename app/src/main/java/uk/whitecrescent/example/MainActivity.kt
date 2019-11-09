package uk.whitecrescent.example

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.github.basshelal.unsplashpicker.data.UnsplashPhoto
import com.github.basshelal.unsplashpicker.presentation.PhotoSize
import com.github.basshelal.unsplashpicker.presentation.UnsplashPhotoPicker
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        UnsplashPhotoPicker.show(this) {
            hasSearch = true
            clickOpensPhoto = false
            longClickSelectsPhoto = false
            isMultipleSelection = true

            // below is the default behavior (excluding snackbars)
            // if you set clickOpensPhoto and longClickSelectsPhoto to true
            // but we've done it explicitly for demonstration purposes
            onClickPhoto = { unsplashPhoto: UnsplashPhoto, imageView: ImageView ->
                showPhoto(unsplashPhoto, PhotoSize.REGULAR)
                shortSnackbar("Clicked ${unsplashPhoto.user.name}")
            }
            onLongClickPhoto = { unsplashPhoto: UnsplashPhoto, imageView: ImageView ->
                selectPhoto(unsplashPhoto)

                Snackbar.make(
                    this,
                    "Selected ${selectedPhotos.size} Photos",
                    Snackbar.LENGTH_SHORT
                ).apply {
                    setAction("Clear Selection") { clearSelectedPhotos() }
                }.show()
            }
        }
    }

    fun View.shortSnackbar(text: String) {
        Snackbar.make(this, text, Snackbar.LENGTH_SHORT).apply {
            setAction("OK") { dismiss() }
        }.show()
    }

    fun View.longSnackbar(text: String) {
        Snackbar.make(this, text, Snackbar.LENGTH_LONG).apply {
            setAction("OK") { dismiss() }
        }.show()
    }

}
