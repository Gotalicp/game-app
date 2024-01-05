package com.example.game_app.ui.main.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.game_app.data.SharedInformation

class ProfileViewModel(application: Application) : AndroidViewModel(application)  {
    private val acc = SharedInformation.getAcc()

}