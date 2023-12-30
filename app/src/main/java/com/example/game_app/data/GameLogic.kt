package com.example.game_app.data

interface GameLogic <T>{
    fun startGame(seed : Long, players : MutableList<PlayerInfo>)
    fun turnHandling(t: T)
    fun endGame(): Boolean
}