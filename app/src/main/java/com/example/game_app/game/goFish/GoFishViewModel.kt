package com.example.game_app.game.goFish

import android.content.IntentSender.OnFinished
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.game_app.FireBaseViewModel
import com.example.game_app.SharedInformation
import com.example.game_app.data.Account
import com.example.game_app.data.LobbyInfo
import com.example.game_app.server.ClientClass
import com.example.game_app.server.ServerHandler
import kotlin.random.Random

class GoFishViewModel : ViewModel() {
    private lateinit var server : ServerHandler<Play>
    private lateinit var client : ClientClass<Play>
    private lateinit var viewModel: FireBaseViewModel
    private var goFishLogic = GoFishLogic()

    private val sharedAccount: LiveData<Account> = SharedInformation.getAcc()

    fun createGame(lobby: LobbyInfo){
        server = ServerHandler(goFishLogic,lobby)
        server.start()
    }
    fun joinGame(uid: String){
        viewModel.getLobby(uid) {
            client = ClientClass(goFishLogic, it!!)
            client.start()
        }
    }
    fun startGame(){
        var seed = Random.nextLong()
        server.startGame(seed)
    }
    fun write(t:Play){
        if (::server.isInitialized) {
            server.send(t)
        } else if (::client.isInitialized) {
            client.write(t)
        }
    }
    fun disconnect(){
        client.disconnect()
    }
    fun stopServer(finish: () -> Unit){
        server.endGame()
    }
}