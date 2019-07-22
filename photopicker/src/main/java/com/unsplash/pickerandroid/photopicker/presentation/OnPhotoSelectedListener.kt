package com.unsplash.pickerandroid.photopicker.presentation

import android.widget.ImageView
import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto

interface OnPhotoSelectedListener {

    fun onPhotosSelected(photos: List<UnsplashPhoto>)

    fun onPhotoLongClick(photo: UnsplashPhoto, imageView: ImageView)
}
