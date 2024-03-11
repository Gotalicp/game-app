package com.example.game_app.ui.game

sealed interface GameStates {
    data object Loading : GameStates
    data class PreGame(val host: Boolean) : GameStates
    data class MyTurn(
        val isYourTurn: Boolean,
        val playerUid: String,
        val playerName: String,
    ) : GameStates

    data object EndGame : GameStates
    data class StartingIn(val startingIn: Long, val showEnd: Boolean) : GameStates
}