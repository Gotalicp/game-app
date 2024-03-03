package com.example.game_app.ui.game

import android.view.View

data class GameUiModel(
    val inGame: Boolean = false,
    val isYourTurn: Boolean = false,
    val showPopup: Boolean = false,
    val showLobby: Boolean = false,
    val startingIn: Long? = null,
    val startingInVisibility: Int = View.GONE,
    val playerName: String? = null,
    val playerUid: String? = null,
    val host: Boolean? = null
)