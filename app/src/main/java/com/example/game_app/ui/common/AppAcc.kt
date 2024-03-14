package com.example.game_app.ui.common

import android.graphics.Bitmap
import java.io.Serializable

data class AppAcc (
    var username: String,
    val uid: String,
    var image: Bitmap?
): Serializable