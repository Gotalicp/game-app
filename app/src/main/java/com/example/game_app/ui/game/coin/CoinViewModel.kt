package com.example.game_app.ui.game.coin

import android.app.Application
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import com.example.game_app.R

class CoinViewModel(application: Application) : AndroidViewModel(application) {
    var curSide: Int = R.drawable.coin_head
    private var stayTheSame = false
    private var isFlipping = false

    private fun nextRandom() {
        stayTheSame = Math.random() < 0.5
    }

    private fun getOtherSide() =
        if (curSide == R.drawable.coin_head) R.drawable.coin_back else R.drawable.coin_head

    fun ImageView.getNextOutCome() {
        if (!isFlipping) {
            nextRandom()
            val otherSide = getOtherSide()
            startAnimation(Rotate3dAnimation(this, curSide, otherSide).apply {
                if (stayTheSame) {
                    setRepeatCount(5)
                } else {
                    curSide = otherSide
                    setRepeatCount(6)
                }
                setDuration(110)
                interpolator = LinearInterpolator()
                setAnimationListener(object : AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        isFlipping = true
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        isFlipping = false
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                        isFlipping = true
                    }
                })
            })
        }
    }
}