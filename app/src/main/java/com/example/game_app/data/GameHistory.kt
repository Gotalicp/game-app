package com.example.game_app.data

import java.io.Serializable

@kotlinx.serialization.Serializable
data class GameHistory(
    val players:Map<String,Int>,
    var date:String?,
    val game: String

): Serializable