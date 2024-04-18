package com.example.game_app.ui.game

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class GivingCardAnimation(
    private val view: View,
    viewTo: View,
    viewFrom: View
) : Animation() {
    private val from = IntArray(2)
    private val to = IntArray(2)

    init {
        duration = 2000
        viewFrom.getLocationOnScreen(from)
        viewTo.getLocationOnScreen(to)
        setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                view.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation) {
                view.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    private var deltaX1: Float = 0f
    private var deltaY1: Float = 0f

    private var deltaX2: Float = 0f
    private var deltaY2: Float = 0f

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        view.translationX = if (interpolatedTime < 0.5f) {
            from[0] + deltaX1 * (interpolatedTime / 0.5f) - view.height / 2
        } else {
            from[0] + deltaX1 + deltaX2 * (interpolatedTime - 0.5f) / 0.5f - view.height / 2
        }
        view.translationY = if (interpolatedTime < 0.5f) {
            from[1] + deltaY1 * (interpolatedTime / 0.5f) - view.height / 2
        } else {
            from[1] + deltaY1 + deltaY2 * (interpolatedTime - 0.5f) / 0.5f - view.height / 2
        }
    }

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
        (parentWidth / 2f).let {
            deltaX1 = it - from[0]
            deltaX2 = to[0] - it
        }
        (parentHeight / 2f).let {
            deltaY1 = it - from[1]
            deltaY2 = to[1] - it
        }
    }

    override fun willChangeBounds() = true
}
