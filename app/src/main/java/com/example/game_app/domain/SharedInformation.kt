package com.example.game_app.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.game_app.data.LobbyInfo
import com.example.game_app.ui.common.AppAcc
import kotlinx.coroutines.flow.MutableStateFlow

//An object of information to share between all fragments and viewModels
object SharedInformation {
    //TODO(save me)
    //Current User
    private val account = MutableLiveData<AppAcc>()
    fun getAcc() = account
    fun updateAcc(acc: AppAcc) {
        account.value = acc
    }

    private val lobby = MutableLiveData<LobbyInfo>()
    fun getLobby(): LiveData<LobbyInfo> = lobby
    fun updateLobby(lobbyInfo: LobbyInfo?) {
        lobby.postValue(lobbyInfo ?: LobbyInfo())
    }

    private val logged = MutableStateFlow(true)
    fun getLogged() = logged
    fun updateLogged(log: Boolean) {
        logged.value = log
    }
}