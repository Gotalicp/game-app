package com.example.game_app.domain

import androidx.lifecycle.MutableLiveData
import com.example.game_app.data.LobbyInfo

object LobbyProvider {
    private val lobby = MutableLiveData<LobbyInfo>()
    fun getLobby() = lobby
    fun updateLobby(lobbyInfo: LobbyInfo?) {
        lobby.postValue(lobbyInfo ?: LobbyInfo())
    }
}