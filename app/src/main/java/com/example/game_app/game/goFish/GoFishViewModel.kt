package com.example.game_app.game.goFish

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.example.game_app.FireBaseUtility
import com.example.game_app.SharedInformation
import com.example.game_app.data.LobbyInfo
import com.example.game_app.server.ClientClass
import com.example.game_app.server.ServerHandler
import kotlin.random.Random

class GoFishViewModel : ViewModel() {
    private lateinit var server : ServerHandler<Play>
    private lateinit var client : ClientClass<Play>
    private var firebaseUtility = FireBaseUtility()
    var goFishLogic = GoFishLogic()

    val sharedAccount = SharedInformation.getAcc().value?.uid

    fun getDeck() = goFishLogic.getDeckSize()
    fun createGame(lobby: LobbyInfo){
        server = ServerHandler(goFishLogic,lobby)
        server.start()
    }
    @SuppressLint("SuspiciousIndentation")
    fun joinGame(uid: String){
        firebaseUtility.getLobby(uid){
            if(it != null)
            client = ClientClass(goFishLogic, it)
            client.start()
        }
    }
    fun startGame(){
        val seed = Random.nextLong()
        server.startGame(seed)
    }
    fun write(t:Play){
        Thread {
            if (::server.isInitialized) {
                server.send(t)
            } else if (::client.isInitialized) {
                client.write(t)
            }
            goFishLogic.turnHandling(t)
        }.start()
    }
    fun disconnect(){
        client.disconnect()
    }
    fun stopServer(){
        server.endGame()
    }
}