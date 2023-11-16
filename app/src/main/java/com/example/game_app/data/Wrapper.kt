package com.example.game_app.data

import java.io.Serializable

data class Wrapper<T>(val t:T?, val seed: Long?):Serializable