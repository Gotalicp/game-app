package com.example.game_app.ui.main.menu

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.game_app.data.firebase.FireBaseUtilityLobby

class MenuViewModel(private val application: Application) : AndroidViewModel(application) {
    private val fireBaseUtilityLobby = FireBaseUtilityLobby()
    fun join(code: String, clazz: Class<*>, callback: (Intent?) -> Unit) {
        try {
            if (code.length == 6) {
                fireBaseUtilityLobby.useCode(code) {
                    it?.let {
                        if (it.players.size < it.maxPlayerCount) {
                            callback(
                                Intent(application.applicationContext, clazz).apply {
                                    putExtra("lobbyUid", it.lobbyUid)
                                    putExtra("lobbyIp", it.ownerIp)
                                    putExtra("code", code)
                                })
                        }
                    }

                }
            }
        } catch (ex: Exception) {
            Log.d("Menu joining error", "$ex")
        }
    }

    fun <T> host(clazz: Class<T>) = Intent(application.applicationContext, clazz)
}