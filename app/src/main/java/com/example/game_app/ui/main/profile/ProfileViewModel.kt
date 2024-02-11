package com.example.game_app.ui.main.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.game_app.domain.SharedInformation
import com.example.game_app.domain.bitmap.BitmapReverser
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    val acc = SharedInformation.getAcc()
    fun getImage() = acc.value?.image?.let { BitmapReverser().adapt(it) }
    fun getEmail() = Firebase.auth.currentUser?.email
}