package com.example.game_app.game

import com.example.game_app.data.PlayerInfo

data class GameData(
    val isWinConditionMet : Boolean = false,
    val scores : MutableList<Double>,
    val players : MutableList<PlayerInfo>,
)
data class StartGame(
    val playerNumber: Int,
    val gameSeed: Long,
)