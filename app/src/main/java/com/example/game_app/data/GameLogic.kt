package com.example.game_app.data

interface GameLogic <T>{

    fun startGame(seed : Long)
    fun setPlayer(players : MutableList<PlayerInfo>)
    fun turnHandling(t: T)
    fun checkEndGame(): Boolean
}