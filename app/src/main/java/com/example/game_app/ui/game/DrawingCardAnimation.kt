package com.example.game_app.ui.game

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class DrawingCardAnimation(
    private val view: View,
    viewTo: View
) : Animation() {
    private val to = IntArray(2)
    private var fromX: Float = 0.0f
    private var fromY: Float = 0.0f

    private var deltaX1: Float = 0f
    private var deltaY1: Float = 0f

    init {
        duration = 1000
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
    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        view.translationX = fromX + deltaX1 * interpolatedTime - view.width / 2
        view.translationY = fromY + deltaY1 * interpolatedTime - view.height / 2
    }

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
        fromX = parentWidth / 2f
        fromY = parentHeight / 2f

        deltaX1 = to[0] - fromX
        deltaY1 = to[1] - fromY
    }

    override fun willChangeBounds() = true
}