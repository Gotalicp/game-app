package com.example.game_app.ui.game.goFish

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.game_app.ui.common.AppAcc
import com.example.game_app.data.PlayerCache
import com.example.game_app.domain.SharedInformation
import com.example.game_app.data.FireBaseUtility
import com.example.game_app.domain.game.Card
import com.example.game_app.domain.game.GoFishLogic
import com.example.game_app.domain.game.Rank
import com.example.game_app.domain.server.OkClient
import com.example.game_app.domain.server.OkServer
import com.example.game_app.domain.server.ServerInterface
import com.example.game_app.ui.game.goFish.popup.EndScreenPopup
import com.example.game_app.ui.game.goFish.popup.LobbyPopup
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.random.Random

class GoFishViewModel(private val application: Application) : AndroidViewModel(application) {
    //States
    sealed interface State {
        data object Loading : State
        data object PreGame : State
        data class MyTurn(
            val isYourTurn: Boolean,
            val playerToTakeTurn: String,
            val visibility: Int
        ) : State

        data object EndGame : State
        data class StartingIn(val startingIn: Long) : State
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    //Server
    private var server: ServerInterface<GoFishLogic.Play>? = null

    var players: List<AppAcc>? = null
    private var rounds: Int? = null

    private var uid = SharedInformation.getAcc().value?.uid
    private var lobby = SharedInformation.getLobby()
    private var cache = PlayerCache.instance
    var goFishLogic = GoFishLogic()

    //PopUps
    private lateinit var lobbyPopup: LobbyPopup

    val id = SharedInformation.getAcc().value?.uid

    init {
        viewModelScope.launch {
            goFishLogic.hasEnded.onEach {
                collectHasEnded(it)
            }.launchIn(this)
            goFishLogic.seed.onEach {
                it?.let { collectSeed(it) }
            }.launchIn(this)
            goFishLogic.playerToTakeTurn.onEach {
                it?.let { collectPlayer(it) }
            }.launchIn(this)
        }
    }

    private suspend fun collectPlayer(player: String) {
        Log.d("GoFishViewModel", "Player To Take Turn: $player")
        _state.postValue(cache.get(player)?.username?.let { name ->
            State.MyTurn((player == uid), name, View.VISIBLE)
        })
        delay(3000L)
        _state.postValue(cache.get(player)?.username?.let { name ->
            State.MyTurn((player == uid), name, View.GONE)
        })
    }

    private suspend fun collectSeed(seed: Long) {
        if (players == null) {
            setUp()
        }
        Log.d("GoFishViewModel", "Seed: $seed")
        _state.postValue(State.StartingIn(5000L))
        delay(5000L)
        goFishLogic.startGame(seed)
    }

    private suspend fun collectHasEnded(hasEnded: Boolean) {
        if (hasEnded) {
            _state.value =
                State.EndGame
            rounds?.let {
                if (rounds != 0) {
                    rounds = rounds!! - 1
                    _state.postValue(State.StartingIn(5000L))
                    delay(5000L)
                    createSeed()
                }
            }
        }
    }

    private suspend fun setUp() {
        players = lobby.value?.players?.map {
            PlayerCache.instance.get(it)!!
        }?.toList()
        rounds = lobby.value?.rounds
    }


    fun findPlayers() =
        goFishLogic.gamePlayers.value?.associateBy { it.uid }?.let { gamePlayersMap ->
            players?.mapNotNull { player ->
                gamePlayersMap[player.uid]?.let { gamePlayer ->
                    Pair(gamePlayer.deck, player)
                }
            }
        }?.partition { it.second.uid == uid }

    fun findMyDeck() = goFishLogic.gamePlayers.value?.find { uid == it.uid }?.deck?.sortedBy { it.rank }

    //Server Part
    fun joinGame(code: String? = null, uid: String? = null, ip: String? = null, context: Context) {
        server = if (code != null && uid != null && ip != null) {
            OkClient(
                gameLogic = goFishLogic,
                expectedTClazz = GoFishLogic.Play::class.java,
                ip = ip,
                code = code,
                port = 8888
            ).apply {
                join()
                lobbyPopup = LobbyPopup(context, false) { createSeed() }

            }
        } else {
            OkServer(
                gameLogic = goFishLogic,
                expectedTClazz = GoFishLogic.Play::class.java,
                port = 8888
            ).apply {
                join()
                lobbyPopup = LobbyPopup(context, true) { createSeed() }
            }
        }
        _state.value = State.PreGame
    }

    private fun createSeed() {
        Random.nextLong().let { seed ->
            goFishLogic.updateSeed(seed)
            server?.send(seed)
        }
    }

    fun write(player: String, card: Rank) {
        viewModelScope.launch {
            server?.send(uid?.let { GoFishLogic.Play(it, player, card) })
        }
    }

    fun disconnect() {
        server?.disconnect()
        server = null
    }

    //Ui Part
    fun showLobby(data: Boolean, view: View) {
        try {
            if (data) {
                lobbyPopup.showPopup(view)
            } else {
                lobbyPopup.dismissPopup()
            }
        } catch (_: ExceptionInInitializerError) {
        }
    }

    fun showEndScreen(view: View, check: Boolean) {
        if (check) {
            goFishLogic.gamePlayers.value?.let {
                EndScreenPopup(
                    application.applicationContext,
                    it
                ).showPopup(view)
            }
        }
    }
}