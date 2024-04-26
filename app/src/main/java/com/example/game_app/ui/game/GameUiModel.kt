package com.example.game_app.ui.game

data class GameUiModel(
    val inGame: Boolean = false,
    val isYourTurn: Boolean = false,
    val showEnd: Boolean = false,
    val showLobby: Boolean = false,
    val startingIn: Long? = null,
    val playerName: String? = null,
    val playerUid: String? = null,
    val host: Boolean? = null,
    val reloaud: Boolean? = null
)