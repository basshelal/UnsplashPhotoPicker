package com.github.basshelal.unsplashpicker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * This type is only used to find out if an [UnsplashPhoto] is a sponsored photo or not.
 *
 * To find if an [UnsplashPhoto] is sponsored you can use [UnsplashPhoto.isSponsored] or
 * check if its [UnsplashPhoto.sponsorship] is NOT `null` meaning it is sponsored
 */
@Parcelize
data class UnsplashSponsorship(
    private val impressions_id: Int?
) : Parcelable