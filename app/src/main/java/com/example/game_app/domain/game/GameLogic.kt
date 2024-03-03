package com.example.game_app.domain.game

import kotlinx.coroutines.flow.Flow

interface GameLogic<T> {
    val hasEnded: Flow<Boolean>
    val playerToTakeTurn: Flow<String?>
    val seed: Flow<Long?>
    suspend fun startGame(seed: Long)
    fun setPlayer(players: MutableList<String>)
    suspend fun turnHandling(t: T)
    fun checkEndGame(): Boolean
    fun updateSeed(seed: Long)
}