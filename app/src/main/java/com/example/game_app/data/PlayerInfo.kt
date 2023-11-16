package com.example.game_app.data

import kotlinx.serialization.Serializable

@Serializable
data class PlayerInfo (
    val username: String,
    val uid : String,
    val isHost: Boolean,
    val image: String?
):java.io.Serializable