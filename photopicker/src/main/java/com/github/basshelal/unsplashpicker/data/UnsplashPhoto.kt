package com.github.basshelal.unsplashpicker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

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
    val sponsorship: UnsplashSponsorship?
) : Parcelable {

    /**
     * Determines whether this [UnsplashPhoto] is sponsored or not
     *
     * See more at the [official Unsplash website](https://unsplash.com/brands)
     */
    inline val isSponsored: Boolean get() = sponsorship != null
}
