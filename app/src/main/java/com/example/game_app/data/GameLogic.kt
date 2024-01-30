package com.example.game_app.data

interface GameLogic <T>{

    fun startGame(seed : Long)
    fun setPlayer(players : MutableList<String>)
    fun turnHandling(t: T)
    fun checkEndGame(): Boolean
    fun updateSeed(seed:Long)
}