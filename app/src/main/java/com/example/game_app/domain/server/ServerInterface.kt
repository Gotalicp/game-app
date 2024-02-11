package com.example.game_app.domain.server

import com.example.game_app.domain.game.GameLogic

interface ServerInterface<T> {
    val gameLogic: GameLogic<T>
    val expectedTClazz: Class<T>
    val port: Int
    fun join()
    fun disconnect()
    fun <T> send(data: T)
}