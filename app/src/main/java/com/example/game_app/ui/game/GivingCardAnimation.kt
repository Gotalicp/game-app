package com.example.game_app.ui.game

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class GivingCardAnimation(
    private val view: View,
    private val fromX: Float,
    private val fromY: Float,
    private val toX: Float,
    private val toY: Float
) : Animation() {
    private var deltaX1: Float = 0f
    private var deltaY1: Float = 0f

    private var deltaX2: Float = 0f
    private var deltaY2: Float = 0f

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        view.translationX = if (interpolatedTime < 0.5f) {
            fromX + deltaX1 * (interpolatedTime / 0.5f)
        } else {
            fromX + deltaX1 + deltaX2 * (interpolatedTime - 0.5f) / 0.5f
        }
        view.translationY = if (interpolatedTime < 0.5f) {
            fromY + deltaY1 * (interpolatedTime / 0.5f)
        } else {
            fromY + deltaY1 + deltaY2 * (interpolatedTime - 0.5f) / 0.5f
        }
    }

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)

        (parentWidth / 2f - view.width / 2).let { x ->
            (parentHeight / 2f - view.height / 2f).let { y ->
                deltaX1 = x - fromX
                deltaY1 = y - fromY

                deltaX2 = toX - x
                deltaY2 = toY - y
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
