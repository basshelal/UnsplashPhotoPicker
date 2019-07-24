package com.github.basshelal.unsplashpicker.presentation

import android.widget.ImageView
import com.github.basshelal.unsplashpicker.data.UnsplashPhoto

internal interface OnPhotoSelectedListener {

    fun onClickPhoto(photo: UnsplashPhoto, imageView: ImageView)

    fun onLongClickPhoto(photo: UnsplashPhoto, imageView: ImageView)
}
