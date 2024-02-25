package com.example.game_app.domain

import kotlinx.coroutines.flow.MutableStateFlow

object SharedInformation {

    private val logged = MutableStateFlow(true)
    fun getLogged() = logged
    fun updateLogged(log: Boolean) {
        logged.value = log
    }
}