package com.example.game_app.ui.game.goFish

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.game_app.data.PlayerCache
import com.example.game_app.data.SharedInformation
import com.example.game_app.domain.server.OkClientClass
import com.example.game_app.domain.server.OkServerClass
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo
import kotlinx.coroutines.launch
import kotlin.random.Random

class GoFishViewModel(application: Application) : AndroidViewModel(application) {
    sealed interface State {
        data class Loading(val showLoading: Boolean) : State
        data class PreGame(val showLobbyInfo: Boolean) : State
        data class MyTurn(val allowPlay: Boolean, val playerToTakeTurn: String) : State
        data class EndGame(val showScores: Boolean) : State
        data class StartingIn(val startingIN: Int) : State
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private lateinit var server: OkServerClass<Play>
    private lateinit var client: OkClientClass<Play>
    private var lobby = SharedInformation.getLobby()
    private var cache = PlayerCache.instance
    var goFishLogic = GoFishLogic()

    val uid = SharedInformation.getAcc().value?.uid
    fun createGame() {
        server = OkServerClass(goFishLogic)
    }

    fun joinGame(uid: String, ip: String) {
        client = OkClientClass(goFishLogic, ConnectionInfo(ip, 8888), uid).apply { join() }
    }

    fun initGame() {
        var rounds = lobby.value?.rounds
        lobby.value?.players?.let { players ->
            goFishLogic.setPlayer(players)
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
    }

    private fun startGame(time: Int, seed: Long? = null) {
        _state.value = State.StartingIn(time)
        seed?.let {
            goFishLogic.startGame(it)
        } ?: run {
            Random.nextLong().let {
                goFishLogic.startGame(it)
                if (::server.isInitialized) {
                    server.send(it)
                }
            }
        }
        goFishLogic.playerToTakeTurn.observeForever { player ->
            viewModelScope.launch {
                _state.value = cache.get(player.uid)?.username?.let {
                    State.MyTurn((player.uid == uid), it)
                }
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
        }
    }

    fun showLobby() {
        _state.value = State.PreGame(true)
    }

    fun disconnect() {
        if (::client.isInitialized) {
            client.disconnect()
        }
    }
}