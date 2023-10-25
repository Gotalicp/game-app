package com.example.game_app.data

import android.graphics.Bitmap

data class PlayerInfo (
    val username: String,
    val uid : String,
    val isHost: Boolean,
    val image: Bitmap
    )