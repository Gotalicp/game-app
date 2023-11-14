package com.example.game_app.game

interface GameLogic <Turn>{
    val playerNumber : Int
    val players : List<Int>
    val roundSeed : Long
    fun startGame()
    fun turnHandling(t: Turn)
    fun gameEnded(): Boolean
    fun endGame()
}