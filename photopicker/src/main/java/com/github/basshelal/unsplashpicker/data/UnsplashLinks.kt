package com.github.basshelal.unsplashpicker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Used to get link relations of any entity (photo, user, etc) in the Unsplash API.
 * Note that not all entities will have all of the below values (hence the nullables.
 * You must consult the [official Unsplash API documentation](https://unsplash.com/documentation)
 * to know which entities have which link relations.
 *
 * All entities have at least a [self] link and a [html] link.
 */
@Parcelize
data class UnsplashLinks(
        val self: String,
        val html: String,
        val photos: String?,
        val likes: String?,
        val portfolio: String?,
        val download: String?,
        val download_location: String?
) : Parcelable
