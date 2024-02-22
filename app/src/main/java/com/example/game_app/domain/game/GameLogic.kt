package com.example.game_app.domain.game

interface GameLogic <T>{

    suspend fun startGame(seed : Long)
    fun setPlayer(players : MutableList<String>)
    suspend fun turnHandling(t: T)
    fun checkEndGame(): Boolean
    fun updateSeed(seed:Long)
}