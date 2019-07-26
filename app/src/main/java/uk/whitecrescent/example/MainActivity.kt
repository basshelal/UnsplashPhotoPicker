package uk.whitecrescent.example

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
            onClickPhoto = { p, i -> shortSnackbar(p.user.name) }
            onLongClickPhoto = { p, i -> showPhoto(p) }
        }
    }

    fun View.shortSnackbar(text: String) {
        Snackbar.make(this, text, Snackbar.LENGTH_SHORT).apply {
            setAction("Dismiss", { dismiss() })
        }.show()
    }
}
