package com.example.game_app.data

import kotlinx.serialization.Serializable

@Serializable
data class PlayerInfo (
    val uid : String,
    val username: String,
    val image: String,
    val isHost: Boolean,
):java.io.Serializable