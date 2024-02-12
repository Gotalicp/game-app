package com.example.game_app.ui.game.goFish

import android.view.View

data class GoFishUiModel(
    val inGame: Boolean = false,
    val isYourTurn: Boolean = false,
    val showScores: Boolean = false,
    val showLobby: Boolean = false,
    val startingIn: Long = 0,
    val startingInVisibility: Int = View.GONE,
    val playerToTakeTurn: String? = null,
    val playerToTakeTurnVisibility:Int = View.GONE
)