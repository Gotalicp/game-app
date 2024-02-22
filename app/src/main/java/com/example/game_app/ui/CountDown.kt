package com.example.game_app.ui

import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ProgressBar

class CountDown(
    private val progressBar: ProgressBar,
    private val onFinishCallback: () -> Unit,
    millisInFuture: Long, countDownInterval: Long
) : CountDownTimer(millisInFuture, countDownInterval) {

    init {
        progressBar.visibility = View.VISIBLE
        progressBar.max = (millisInFuture / 1000).toInt()
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
        onFinishCallback
        progressBar.progress = 0
        progressBar.visibility = View.INVISIBLE
    }
}