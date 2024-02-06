package com.example.game_app.ui.main.menu

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.game_app.domain.FireBaseUtility
import kotlinx.coroutines.launch

class MenuViewModel(private val application: Application) : AndroidViewModel(application) {

    private val fireBaseUtility = FireBaseUtility()
    fun join(code: String, clazz: Class<*>) =
        Intent(application.applicationContext, clazz).apply {
            viewModelScope.launch {
                fireBaseUtility.useCode(code) {
                    putExtra("lobbyUid", it?.first)
                    putExtra("lobbyIp", it?.second)
                }
            }
        }

    fun <T> host(clazz: Class<T>) = Intent(application.applicationContext,clazz)
}