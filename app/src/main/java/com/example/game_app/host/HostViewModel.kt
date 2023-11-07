package com.example.game_app.host

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.game_app.FireBaseViewModel
import com.example.game_app.data.LobbyInfo
import com.example.game_app.data.Messages
import com.example.game_app.data.PlayerInfo
import com.example.game_app.login.ui.login.AuthenticationViewModel
import com.example.game_app.server.ServerClass

class HostViewModel : ViewModel() {
    private val _messages = MutableLiveData<List<Messages>>()
    val messages: LiveData<List<Messages>> get() = _messages
    private lateinit var server : ServerClass
    private var fireBaseViewModel = FireBaseViewModel()
    private var acc = AuthenticationViewModel()

    fun start(){
        server = ServerClass()
        Log.d("starting","${acc.acc.value}")
        acc.acc.value?.let {
            fireBaseViewModel.hostLobby(
                LobbyInfo(
                    lobbyName = "test",
                    ownerIp = server.getLocalInetAddress()!!,
                    lobbyUid = it.uid!!,
                    connection = "internet",
                    maxPlayerCount = 2,
                    gamemode = "gamemode",
                    players = mutableListOf(PlayerInfo(it.username!!, it.uid, false, it.image!!)),
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