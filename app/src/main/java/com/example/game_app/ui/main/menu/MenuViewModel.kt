package com.example.game_app.ui.main.menu

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.game_app.data.FireBaseUtility
import com.example.game_app.data.LobbyInfo
import kotlinx.coroutines.launch

class MenuViewModel(private val application: Application) : AndroidViewModel(application) {
    private val fireBaseUtility = FireBaseUtility()
    fun join(code: String, clazz: Class<*>, callback: (Intent?) -> Unit) {
        Log.d("code", code)
        try {
            if (code.length == 6) {
                fireBaseUtility.useCode(code) {
                    it?.let {
                        callback(
                            Intent(application.applicationContext, clazz).apply {
                                putExtra("lobbyUid", it.lobbyUid)
                                putExtra("lobbyIp", it.ownerIp)
                                putExtra("code", code)
                            })
                    }

                }
            }
        }catch (_:Exception){}
    }

    fun <T> host(clazz: Class<T>) = Intent(application.applicationContext, clazz)
}