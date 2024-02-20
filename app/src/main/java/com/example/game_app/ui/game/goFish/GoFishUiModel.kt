package com.example.game_app.ui.game.goFish

import android.view.View

data class GoFishUiModel(
    val inGame: Boolean = false,
    val isYourTurn: Boolean = false,
    val showScores: Boolean = false,
    val showLobby: Boolean = false,
    val startingIn: Long? = null,
    val startingInVisibility: Int = View.GONE,
    val playerName: String? = null,
    val playerUid: String? = null,
)