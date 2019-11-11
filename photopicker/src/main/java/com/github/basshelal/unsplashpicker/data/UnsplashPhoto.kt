package com.github.basshelal.unsplashpicker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/**
 * See the [official Unsplash API Photos section](https://unsplash.com/documentation#photos)
 */
@Parcelize
data class UnsplashPhoto(
        val id: String,
        val created_at: String,
        val width: Int,
        val height: Int,
        val color: String? = "#000000",
        val likes: Int,
        val description: String?,
        val urls: UnsplashUrls,
        val links: UnsplashLinks,
        val user: UnsplashUser,
        private val sponsorship: @RawValue Any?
) : Parcelable {

    /**
     * Determines whether this [UnsplashPhoto] is sponsored or not
     *
     * See more at the [official Unsplash website](https://unsplash.com/brands)
     */
    val isSponsored: Boolean get() = sponsorship != null
}
