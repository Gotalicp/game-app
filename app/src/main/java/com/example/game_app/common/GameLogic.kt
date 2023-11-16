package com.example.game_app.common

import com.example.game_app.data.PlayerInfo

interface GameLogic <T>{
    val players : MutableList<PlayerInfo>
    fun startGame(seed : Long)
    fun turnHandling(t: T)
    fun gameEnded(): Boolean
    fun endGame()
}