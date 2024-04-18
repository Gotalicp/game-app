package com.example.game_app.ui.common

import android.os.CountDownTimer
import android.view.View
import android.widget.ProgressBar

class CountDown(
    private val progressBar: ProgressBar,
    private val onFinishCallback: () -> Unit,
    millisInFuture: Long
) : CountDownTimer(millisInFuture, 1000) {

    init {
        progressBar.visibility = View.VISIBLE
        progressBar.max = (millisInFuture / 1000).toInt()
    }

    fun resetCountdown() {
        progressBar.visibility = View.VISIBLE
        progressBar.progress = progressBar.max
        start()
    }

    fun cancelCountdown() {
        cancel()
        progressBar.visibility = View.INVISIBLE
    }

    override fun onTick(p0: Long) {
        progressBar.progress = (p0 / 1000).toInt()
    }

    override fun onFinish() {
        onFinishCallback.invoke()
        progressBar.progress = 0
        progressBar.visibility = View.INVISIBLE
    }
}