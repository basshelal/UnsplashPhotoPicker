package com.unsplash.pickerandroid.photopicker.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.unsplash.pickerandroid.photopicker.R
import kotlinx.android.synthetic.main.photo_picker.view.*

class UnsplashPhotoPicker
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attributeSet, defStyle) {

    // API design to be changed later

    // Views
    var unsplashPhotoPickerRecyclerView: RecyclerView // keep this private?
    var searchLayout: FrameLayout // callers can get this to modify it
    var searchTextView: TextView // callers can get this to modify it

    // XML attributes
    var hasSearch: Boolean = true // xml attribute
    var allowMultipleSelection: Boolean = false // xml attribute TODO, do it later!
    var pageSize: Int = 20 // xml attribute
    var errorImage: Drawable? = null // xml attribute
    var placeHolderImage: Drawable? = null // xml attribute

    // Callbacks
    var onSearchTextChanged: CallBack = {} // callers set this to do stuff on Search, we should provide some default
    var onClickPhoto: CallBack = {} // callers set this to do stuff on click, we should provide some default
    var onLongClickPhoto: CallBack = {} // callers set this to do stuff on long click, we should provide some default

    /* TODO If we allow to show each image upon click on long click we must use Shared Element Transitions */

    init {
        View.inflate(context, R.layout.photo_picker, this)

        require(unsplashPicker_recyclerView != null)
        unsplashPhotoPickerRecyclerView = unsplashPicker_recyclerView

        require(search_cardView != null && search_editText != null)
        searchLayout = search_cardView
        searchTextView = search_editText

        if (!hasSearch) {
            searchLayout.visibility = View.GONE
        }





    }

}

private typealias CallBack = () -> Unit