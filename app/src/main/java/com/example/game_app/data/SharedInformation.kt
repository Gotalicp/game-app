package com.example.game_app.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

//An object of information to share between all fragments and viewModels
object SharedInformation {
    //Current User
    private val account = MutableLiveData<Account>()
    fun getAcc() = account
    fun updateAcc(acc: Account) {
        account.value = acc
    }
    private val lobby = MutableLiveData<LobbyInfo>()
    fun getLobby(): LiveData<LobbyInfo> = lobby
    fun updateLobby(lobbyInfo: LobbyInfo?) {
        lobby.postValue(lobbyInfo?: LobbyInfo())
    }
    private val logged = MutableLiveData<Boolean>()
    fun getLogged() = logged
    fun updateLogged(log: Boolean) {
        logged.value = log
    }
}