package com.example.game_app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.game_app.data.Account
import com.example.game_app.data.LobbyInfo

//An object of information to share between all fragments and viewModels
object SharedInformation {
    //Current User
    private val account = MutableLiveData<Account>()
    fun getAcc(): LiveData<Account> {
        return account
    }
    fun updateAcc(acc: Account) {
        account.value = acc
    }
    private val lobby = MutableLiveData<LobbyInfo>()
    fun getLobby(): LiveData<LobbyInfo> {
        return lobby
    }
    fun updateAcc(lobbyInfo: LobbyInfo) {
        lobby.value = lobbyInfo
    }
}