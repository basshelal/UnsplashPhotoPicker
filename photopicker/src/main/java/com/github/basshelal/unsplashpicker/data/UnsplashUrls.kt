package com.github.basshelal.unsplashpicker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * See the [official Unsplash API Images section](https://unsplash.com/documentation#dynamically-resizable-images)
 */
@Parcelize
data class UnsplashUrls(
        val thumb: String?,
        val small: String,
        val regular: String?,
        val full: String?,
        val raw: String?
) : Parcelable
