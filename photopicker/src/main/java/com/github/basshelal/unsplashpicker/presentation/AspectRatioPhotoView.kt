@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.unsplashpicker.presentation

import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import com.github.chrisbanes.photoview.PhotoView

internal class AspectRatioPhotoView
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PhotoView(context, attrs, defStyleAttr) {

    internal var aspectRatio: Double = -1.0
        set(value) {
            field = value
            invalidate()
        }

    init {
        attacher.setZoomInterpolator(AccelerateDecelerateInterpolator())
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (aspectRatio == -1.0) return
        val width = measuredWidth
        val height = (width * aspectRatio).toInt()
        if (height == measuredHeight) return
        setMeasuredDimension(width, height)
    }
}