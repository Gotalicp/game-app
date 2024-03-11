package com.example.game_app.data

import java.io.Serializable

@kotlinx.serialization.Serializable
data class GameHistory(
    val id: String,
    val players: Map<String, Int>,
    val status: String,
    var date: String?,
    val game: String
) : Serializable