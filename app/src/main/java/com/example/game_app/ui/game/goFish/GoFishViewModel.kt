package com.example.game_app.ui.game.goFish

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.game_app.data.SharedInformation
import com.example.game_app.domain.server.ClientClass
import com.example.game_app.domain.server.OkClientClass
import com.example.game_app.domain.server.OkServerClass
import com.example.game_app.domain.server.ServerHandler
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo
import kotlinx.coroutines.launch
import kotlin.random.Random

class GoFishViewModel(application: Application) : AndroidViewModel(application) {
    sealed interface State {
        data class Loading(val showLoading: Boolean) : State
        data class PreGame(val showLobbyInfo: Boolean) : State
        data class MyTurn(val allowPlay: Boolean) : State
        data class EndGame(val showScores: Boolean) : State
        data class StartingIn(val startingIN: Int) : State
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private lateinit var server: OkServerClass<Play>
    private lateinit var client: OkClientClass<Play>
    private var lobby = SharedInformation.getLobby().value
    var goFishLogic = GoFishLogic()

    val uid = SharedInformation.getAcc().value?.uid
    fun createGame() {
        server = OkServerClass(goFishLogic)
    }

    fun joinGame(uid: String, ip: String) {
        client = OkClientClass(goFishLogic, ConnectionInfo("62.176.88.52",8888) ,uid)
        client.join()
    }

    fun initGame() {
        var rounds = lobby?.rounds
        startGame(0)
        viewModelScope.launch {
            goFishLogic.hasEnded.collect { hasEnded ->
                if (hasEnded) {
                    rounds?.let {
                        if (it == 0) {
                            _state.value =
                                State.EndGame(true)
                        } else {
                            rounds = it - 1
                            startGame(10)
                        }
                    }
                }
            }
        }
    }

    private fun startGame(time: Int) {
        _state.value = State.StartingIn(time)
//        server.startGame(Random.nextLong())
        server.send("moqt tekst za teb")
        viewModelScope.launch {
            goFishLogic.playerToTakeTurn.observeForever {
                _state.value = State.MyTurn((goFishLogic.playerToTakeTurn.value?.info?.uid === uid))
            }
        }
    }

    fun write(t: Play) {
        viewModelScope.launch {
            if (::server.isInitialized) {
                server.send(t)
            } else if (::client.isInitialized) {
                client.send(t)
            }
            goFishLogic.turnHandling(t)
            _state.value =
                State.MyTurn((goFishLogic.playerToTakeTurn.value?.info?.uid === uid))
        }
    }

    fun showLobby() {
        _state.value = State.PreGame(true)
    }

    fun disconnect() {
        if(::client.isInitialized){
            client.disconnect()
        }
    }
}