package com.example.game_app.common

import com.example.game_app.data.PlayerInfo

interface GameLogic <T>{
    fun startGame(seed : Long, players : MutableList<PlayerInfo>)
    fun turnHandling(t: T)
    fun gameEnded(): Boolean
    fun endGame()
}