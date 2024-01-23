package com.example.game_app.ui.game.goFish

data class GoFishUiModel(
    val inGame: Boolean = false,
    val isYourTurn: Boolean = false,
    val showScores: Boolean = false,
    val showLobby: Boolean = false,
    val startingIn: Int = 0,
)