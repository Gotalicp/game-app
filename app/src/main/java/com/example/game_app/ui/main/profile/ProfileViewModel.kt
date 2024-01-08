package com.example.game_app.ui.main.profile

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import com.example.game_app.data.SharedInformation
import com.example.game_app.domain.bitmap.BitmapReverser

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    val acc = SharedInformation.getAcc().value

    fun getImage() = acc?.image?.let { BitmapReverser().adapt(it) }
}