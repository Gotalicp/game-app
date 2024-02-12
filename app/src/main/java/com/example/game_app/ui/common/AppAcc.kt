package com.example.game_app.ui.common

import android.graphics.Bitmap
import java.io.Serializable

data class AppAcc (
    val username: String?,
    val uid: String?,
    val image: Bitmap?
): Serializable