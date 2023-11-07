package com.example.game_app.host

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.game_app.FireBaseViewModel
import com.example.game_app.SharedAccount
import com.example.game_app.data.Account
import com.example.game_app.data.LobbyInfo
import com.example.game_app.data.Messages
import com.example.game_app.data.PlayerInfo
import com.example.game_app.server.ServerClass

class HostViewModel : ViewModel() {
    private val _messages = MutableLiveData<List<Messages>>()
    val messages: LiveData<List<Messages>> get() = _messages
    private lateinit var server : ServerClass
    private var fireBaseViewModel = FireBaseViewModel()
    private val sharedAccount: LiveData<Account> = SharedAccount.getAcc()


    fun start(){
        server = ServerClass()
        sharedAccount.value?.let {acc->
            fireBaseViewModel.hostLobby(
                LobbyInfo(
                    lobbyName = "test",
                    ownerIp = server.getLocalInetAddress()!!,
                    lobbyUid = acc.uid!!,
                    connection = "internet",
                    maxPlayerCount = 2,
                    gamemode = "gamemode",
                    players = mutableListOf(PlayerInfo(acc.username!!, acc.uid.toString(), false, null)),
                    gamemodeId = 1
                )) }
        server.start()
    }

    fun send(message: String){
        server.write(Messages("name", message,"now"))
    }
    fun updateMessage(message: Messages){
        _messages.value = _messages.value.orEmpty() + listOf(message)
    }
    fun end(){

    }
}