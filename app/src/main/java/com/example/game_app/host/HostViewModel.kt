package com.example.game_app.host

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.game_app.FireBaseViewModel
import com.example.game_app.SharedInformation
import com.example.game_app.data.Account
import com.example.game_app.data.LobbyInfo
import com.example.game_app.data.Messages
import com.example.game_app.data.PlayerInfo
import com.example.game_app.server.ServerClass

class HostViewModel : ViewModel() {
    private lateinit var server : ServerClass
    private var fireBaseViewModel = FireBaseViewModel()
    private val sharedAccount: LiveData<Account> = SharedInformation.getAcc()

    fun start(){
        server = ServerClass()
        sharedAccount.value?.let {acc->
            fireBaseViewModel.hostLobby(
                LobbyInfo(
                    lobbyName = "test",
                    ownerIp = server.getLocalInetAddress()!!.toString(),
                    lobbyUid = acc.uid!!,
                    connection = "internet",
                    maxPlayerCount = 2,
                    gamemode = "gamemode",
                    players = mutableListOf(PlayerInfo(acc.username!!, acc.uid, false, null)),
                    gamemodeId = 1
                )) }
        server.start()
    }

    fun send(message: String){
        server.write(Messages("name", message,"now"))
    }
    fun end(){
        fireBaseViewModel.destoryLobby(sharedAccount.value!!.uid!!)
        server.close()
    }
}