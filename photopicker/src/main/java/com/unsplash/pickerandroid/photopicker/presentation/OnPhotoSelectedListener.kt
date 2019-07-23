package com.unsplash.pickerandroid.photopicker.presentation

import android.widget.ImageView
import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto

interface OnPhotoSelectedListener {

    fun onClickPhoto(photo: UnsplashPhoto, imageView: ImageView)

    fun onLongClickPhoto(photo: UnsplashPhoto, imageView: ImageView)
}
