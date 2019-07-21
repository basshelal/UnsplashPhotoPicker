package com.unsplash.pickerandroid.photopicker.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UnsplashPhotoPicker
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attributeSet, defStyle) {

    // API design to be changed later

    // Search
    var hasSearch: Boolean = true // xml attribute
    var searchLayout: FrameLayout = FrameLayout(context) // callers can get this to modify it
    var searchTextView: TextView = TextView(context) // callers can get this to modify it
    var onSearchTextChanged: CallBack = {} // callers set this to do stuff on Search, we should provide some default

    var allowMultipleSelection: Boolean = false // xml attribute
    var pageSize: Int = 20 // xml attribute

    var onClickPhoto: CallBack = {} // callers set this to do stuff on click, we should provide some default
    var onLongClickPhoto: CallBack = {} // callers set this to do stuff on long click, we should provide some default

    var errorImage: Drawable = Drawable.createFromPath("") // xml attribute
    var placeHolderImage: Drawable = Drawable.createFromPath("") // xml attribute

    /* TODO If we allow to show each image upon click on long click we must use Shared Element Transitions */

    private var unsplashPhotoPickerRecyclerView: RecyclerView = RecyclerView(context) // keep this private

    init {

    }

}

class UnsplashPhotoPickerRecyclerView
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attributeSet, defStyle) {


    init {

    }
}

private typealias CallBack = () -> Unit