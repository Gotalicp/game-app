package com.example.game_app.ui.main.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.game_app.data.FireBaseUtilityAcc
import com.example.game_app.data.FireBaseUtilityHistory
import com.example.game_app.domain.AccountProvider

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    val acc = AccountProvider.getAcc()
    fun getEmail() = FireBaseUtilityAcc().getEmail()
    
    val history = FireBaseUtilityHistory.history
}