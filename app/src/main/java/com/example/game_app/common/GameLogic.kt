package com.example.game_app.common

interface GameLogic <T>{
    val playerNumber : Int
    val players : Int
    fun startGame(seed : Long)
    fun turnHandling(t: T)
    fun gameEnded(): Boolean
    fun endGame()
}