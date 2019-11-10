@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.unsplashpicker.presentation

import android.content.Context
import android.util.DisplayMetrics
import kotlin.math.roundToInt

/**
 * This method converts dp unit to equivalent pixels, depending on device density.
 *
 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
 * @param context Context to get resources and device specific display metrics
 * @return A float value to represent px equivalent to dp depending on device density
 */
inline internal fun convertDpToPx(dp: Number, context: Context): Int {
    return (dp.F * (context.resources.displayMetrics.densityDpi.F /
            DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

/**
 * This method converts device specific pixels to density independent pixels.
 *
 * @param px A value in px (pixels) unit. Which we need to convert into db
 * @param context Context to get resources and device specific display metrics
 * @return A float value to represent dp equivalent to px value
 */
inline internal fun convertPxToDp(px: Number, context: Context): Int {
    return (px.F / (context.resources.displayMetrics.densityDpi.F /
            DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

inline internal val Number.D: Double
    get() = this.toDouble()

inline internal val Number.L: Long
    get() = this.toLong()

inline internal val Number.F: Float
    get() = this.toFloat()

inline internal val Number.I: Int
    get() = this.toInt()