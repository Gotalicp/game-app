package com.example.game_app.game.goFish

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.game_app.FireBaseViewModel
import com.example.game_app.SharedInformation
import com.example.game_app.data.Account
import com.example.game_app.data.LobbyInfo
import com.example.game_app.server.ClientClass
import com.example.game_app.server.ServerHandler

class GoFishViewModel : ViewModel() {
    private lateinit var server : ServerHandler<Play>
    private lateinit var client : ClientClass<Play>
    private lateinit var viewModel: FireBaseViewModel

    private val sharedAccount: LiveData<Account> = SharedInformation.getAcc()

    fun createGame(lobby: LobbyInfo){
        server = ServerHandler(GoFishLogic(),lobby)
        server.start()
    }
    fun joinGame(lobby: LobbyInfo){
        client = ClientClass(GoFishLogic(),lobby.ownerIp)
        client.start()
    }
}