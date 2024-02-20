package com.example.game_app.ui.game

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class DrawingCardAnimation(
    private val view: View,
    viewTo: View
) : Animation() {
    private val to = IntArray(2)
    init {
        viewTo.getLocationOnScreen(to)
    }
    private val fromX: Float = 0.0f
    private val fromY: Float = 0.0f

    private var deltaX1: Float = 0f
    private var deltaY1: Float = 0f
    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        //TODO(Fix)
        view.translationX = to[0] + deltaX1 * interpolatedTime
        view.translationY = to[1] + deltaY1 * interpolatedTime
    }

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)

        (parentWidth / 2f - view.width / 2).let { x ->
            (parentHeight / 2f - view.height / 2f).let { y ->
                deltaX1 = x - fromX
                deltaY1 = y - fromY
            }
        }
    }

    override fun willChangeBounds() = true

    init {
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
}