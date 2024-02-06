package com.example.game_app.ui.game.goFish

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.game_app.data.Account
import com.example.game_app.data.PlayerCache
import com.example.game_app.data.SharedInformation
import com.example.game_app.domain.FireBaseUtility
import com.example.game_app.domain.GetLocalIp
import com.example.game_app.domain.server.OkClientClass
import com.example.game_app.domain.server.OkServerClass
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class GoFishViewModel(application: Application) : AndroidViewModel(application) {
    sealed interface State {
        data class Loading(val showLoading: Boolean) : State
        data class PreGame(val showLobbyInfo: Boolean) : State
        data class MyTurn(val isYourTurn: Boolean, val playerToTakeTurn: String) : State
        data class EndGame(val showScores: Boolean) : State
        data class StartingIn(val startingIn: Int) : State
    }

    var players: List<Account>? = null

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private lateinit var server: OkServerClass<Play>
    private lateinit var client: OkClientClass<Play>
    private lateinit var endCollector: Unit
    private var lobby = SharedInformation.getLobby()
    private var cache = PlayerCache.instance
    var goFishLogic = GoFishLogic()

    val uid = SharedInformation.getAcc().value?.uid

    init {
        goFishLogic.playerToTakeTurn.observeForever { player ->
            player?.let {
                viewModelScope.launch {
                    Log.d("GoFishViewModel", "Player To Take Turn: $it")
                    _state.postValue(cache.get(it.uid)?.username?.let { name ->
                        State.MyTurn((it.uid == uid), name)
                    })
                }
            }
        }
        viewModelScope.launch {
            goFishLogic.seed.collect { seed ->
                if (seed != null) {
                    if (!::endCollector.isInitialized) {
                        viewModelScope.launch {
                            players = lobby.value?.players?.map {
                                PlayerCache.instance.get(it)!!
                            }?.toMutableList()
                            endCollector = startGame()
                        }
                    }
                    Log.d("GoFishViewModel", "Seed: $seed")
                    _state.postValue(State.StartingIn(5))
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(5000L)
                        goFishLogic.startGame(seed)
                    }
                }
            }
        }
    }

    fun createGame() {
        server = OkServerClass(goFishLogic, Play::class.java)
        FireBaseUtility().hostLobby(GetLocalIp().getLocalInetAddress()?:"", getApplication())
    }

    fun joinGame(uid: String, ip: String) {
        client = OkClientClass(
            goFishLogic,
            Play::class.java,
            ConnectionInfo(ip, 8888),
            uid
        ).apply { join() }
    }


    private fun startGame() {
        Log.d("started", "started")
        var rounds = lobby.value?.rounds
        rounds?.let {
            viewModelScope.launch {
                goFishLogic.hasEnded.collect { hasEnded ->
                    if (hasEnded) {
                        _state.value =
                            State.EndGame(true)
                        if (rounds != 0) {
                            rounds--
                            createSeed()
                        }
                    }
                }
            }
        }
    }

    fun createSeed() {
        if (::server.isInitialized) {
            Random.nextLong().let { seed ->
                goFishLogic.updateSeed(seed)
                server.send(seed)
            }
        }
    }

    fun write(t: Play) {
        viewModelScope.launch {
            if (::server.isInitialized) {
                server.send(t)
                goFishLogic.turnHandling(t)
            } else if (::client.isInitialized) {
                client.send(t)
            }
        }
    }

    fun showLobby() {
        _state.value = State.PreGame(true)
    }

    fun disconnect() {
        if (::client.isInitialized) {
            client.disconnect()
        } else if (::server.isInitialized) {
            server.stopServer()
        }
    }
}
