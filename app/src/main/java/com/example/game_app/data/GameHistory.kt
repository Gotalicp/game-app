package com.example.game_app.data

import java.io.Serializable

@kotlinx.serialization.Serializable
data class GameHistory(
    val players:Map<String,Int>,
    val status: String,
    var date:String?,
    val game: String

): Serializable