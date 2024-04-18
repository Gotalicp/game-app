package com.example.game_app.ui.game.dialogs.lobby

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.game_app.data.LobbyInfo
import com.example.game_app.data.PlayerCache
import com.example.game_app.data.firebase.FireBaseUtilityLobby
import com.example.game_app.ui.common.AppAcc
import kotlinx.coroutines.launch

class LobbyViewModel(application: Application) : AndroidViewModel(application) {
    private val fireBaseUtility = FireBaseUtilityLobby()
    private val cache = PlayerCache.instance

    fun changeSettings(
        boolean: Boolean,
        time: String? = null,
        rounds: Int? = null,
        playerLimit: Int? = null
    ) {
        if (boolean) {
            fireBaseUtility.updateLobby(
                secPerTurn = time,
                rounds = rounds,
                playerLimit = playerLimit
            )
        }
    }

    fun getPlayer(lobby: LobbyInfo, callback: (List<AppAcc>) -> Unit) {
        viewModelScope.launch {
            callback(lobby.players.mapNotNull { cache.get(it) })
        }
    }
}