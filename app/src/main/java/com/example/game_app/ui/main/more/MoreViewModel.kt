package com.example.game_app.ui.main.more

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.game_app.data.firebase.FireBaseUtilityAcc
import kotlinx.coroutines.launch

class MoreViewModel(application: Application) : AndroidViewModel(application) {
    private val firebase = FireBaseUtilityAcc
    fun updateUser(username: String? = null, image: Bitmap? = null) {
        viewModelScope.launch {
            firebase.updateUser(username = username, image = image)
        }
    }
}