package com.unsplash.pickerandroid.photopicker.presentation

import android.widget.ImageView
import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto

interface OnPhotoSelectedListener {

    fun onPhotoSelected(photo: UnsplashPhoto, imageView: ImageView)

    fun onPhotoLongClick(photo: UnsplashPhoto, imageView: ImageView): Boolean
}
