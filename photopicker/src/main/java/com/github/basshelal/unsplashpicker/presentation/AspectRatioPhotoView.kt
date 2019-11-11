@file:Suppress("NOTHING_TO_INLINE")

package com.github.basshelal.unsplashpicker.presentation

import android.content.Context
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import com.github.chrisbanes.photoview.PhotoView

internal class AspectRatioPhotoView
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PhotoView(context, attrs, defStyleAttr) {

    private var dx = 0F
    private var dy = 0F

    private val returnPoint = PointF()

    private var touchPoint = PointF()

    private var downCalled = false

    private var initialRect: RectF? = null

    var draggingEnabled = false

    internal var aspectRatio: Double = -1.0
        set(value) {
            field = value
            invalidate()
        }

    init {
        attacher.setZoomInterpolator(AccelerateDecelerateInterpolator())
        // Conflicts with touch listeners on the PhotoView because the attacher works by attaching its own listener
        setOnTouchListener { v, event ->
            touchPoint.set(event.rawX, event.rawY)
            if (draggingEnabled) {
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        onDown(event)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        onDown(event)
                        onMove(event)
                    }
                    MotionEvent.ACTION_UP -> {
                        endDrag()
                    }
                    else -> attacher.onTouch(v, event)
                }
            }
            attacher.onTouch(v, event)
            true
        }
        setOnMatrixChangeListener {
            if (initialRect == null || initialRect?.isEmpty == true) initialRect = RectF(it)
            if (it == initialRect) draggingEnabled = true
            if (it != initialRect) draggingEnabled = false
        }
        returnPoint.set(this.x, this.y)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (aspectRatio == -1.0) return
        val width = measuredWidth
        val height = (width * aspectRatio).toInt()
        if (height == measuredHeight) return
        setMeasuredDimension(width, height)
    }

    private inline fun onDown(event: MotionEvent) {
        if (!downCalled) {
            //dx = this.x - event.rawX
            dy = this.y - event.rawY
            touchPoint.set(event.rawX, event.rawY)
            downCalled = true
        }
    }

    private inline fun onMove(event: MotionEvent) {
        // this.x = event.rawX + dx
        this.y = event.rawY + dy
        touchPoint.set(event.rawX, event.rawY)
    }

    private inline fun animateReturn() {
        val dampingRatio = 0.6F
        val stiffness = 1000F
        SpringAnimation(this, DynamicAnimation.X, returnPoint.x).also {
            it.spring.dampingRatio = dampingRatio
            it.spring.stiffness = stiffness
        }.start()
        SpringAnimation(this, DynamicAnimation.Y, returnPoint.y).also {
            it.spring.dampingRatio = dampingRatio
            it.spring.stiffness = stiffness
            it.addEndListener { _, _, _, _ -> afterEndAnimation() }
        }.start()
    }

    private inline fun afterEndAnimation() {
        downCalled = false
    }

    fun endDrag() {
        animateReturn()
    }
}