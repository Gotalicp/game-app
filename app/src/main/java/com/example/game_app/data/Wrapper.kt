package com.example.game_app.data

import java.io.Serializable

@kotlinx.serialization.Serializable
data class Wrapper<T>(val t:T?, val seed: Long?):Serializable