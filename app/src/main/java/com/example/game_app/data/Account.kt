package com.example.game_app.data

import java.io.Serializable

@kotlinx.serialization.Serializable
data class Account (
    val username: String?,
    val uid: String?,
    val image: String?
):Serializable