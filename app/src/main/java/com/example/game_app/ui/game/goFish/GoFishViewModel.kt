package com.example.game_app.ui.game.goFish

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.game_app.data.fishy.Account
import com.example.game_app.data.PlayerCache
import com.example.game_app.domain.SharedInformation
import com.example.game_app.data.FireBaseUtility
import com.example.game_app.domain.game.GoFishLogic
import com.example.game_app.domain.game.Rank
import com.example.game_app.domain.server.OkClient
import com.example.game_app.domain.server.OkServer
import com.example.game_app.domain.server.ServerInterface
import com.example.game_app.ui.game.goFish.popup.EndScreenPopup
import com.example.game_app.ui.game.goFish.popup.LobbyPopup
import kotlinx.coroutines.delay
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
        data class StartingIn(val startingIn: Int) : State
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    //Server
    private lateinit var server: ServerInterface<GoFishLogic.Play>

    var players: List<Account>? = null

    private lateinit var endCollector: Unit
    private var lobby = SharedInformation.getLobby()
    private var cache = PlayerCache.instance
    var goFishLogic = GoFishLogic()

    //PopUps
    private lateinit var lobbyPopup: LobbyPopup

    val uid = SharedInformation.getAcc().value?.uid

    init {
        viewModelScope.launch {
            collectSeed()
            collectPlayer()
        }
    }

    private suspend fun collectPlayer() {
        goFishLogic.playerToTakeTurn.collect { player ->
            player?.let {
                viewModelScope.launch {
                    Log.d("GoFishViewModel", "Player To Take Turn: $it")
                    _state.postValue(cache.get(it)?.username?.let { name ->
                        State.MyTurn((it == uid), name, View.VISIBLE)
                    })
                    delay(3000L)
                    _state.postValue(cache.get(it)?.username?.let { name ->
                        State.MyTurn((it == uid), name, View.GONE)
                    })
                }
            }
        }
    }

    private suspend fun collectSeed() {
        goFishLogic.seed.collect { seed ->
            seed?.let {
                if (!::endCollector.isInitialized) {
                    players = lobby.value?.players?.map {
                        PlayerCache.instance.get(it)!!
                    }?.toMutableList()
                    endCollector = startGame()
                }
                Log.d("GoFishViewModel", "Seed: $seed")
                _state.postValue(State.StartingIn(5))
                delay(5000L)
                goFishLogic.startGame(seed)
            }
        }
    }

    private fun startGame() {
        Log.d("started", "started")
        var rounds = lobby.value?.rounds
        rounds?.let {
            viewModelScope.launch {
                goFishLogic.hasEnded.collect { hasEnded ->
                    if (hasEnded) {
                        _state.value =
                            State.EndGame
                        if (rounds != 0) {
                            rounds--
                            _state.postValue(State.StartingIn(5))
                            delay(5000L)
                            createSeed()
                        }
                    }
                }
            }
        }
    }

    fun findElse() = goFishLogic.gamePlayers.value?.associateBy { it.uid }?.let { gamePlayersMap ->
        players?.mapNotNull { player ->
            gamePlayersMap[player.uid]?.let { gamePlayer ->
                Pair(gamePlayer.deck, player)
            }
        }
    }

    fun findMyDeck() = goFishLogic.gamePlayers.value?.find { uid == it.uid }?.deck

    //Server Part
    fun joinGame(uid: String? = null, ip: String? = null, context: Context) {
        server = if (uid != null && ip != null) {
            OkClient(
                gameLogic = goFishLogic,
                expectedTClazz = GoFishLogic.Play::class.java,
                ip = ip,
                lobbyUid = uid,
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
                //TODO(thing about the middle)
                FireBaseUtility().hostLobby("GoFish")
                join()
                lobbyPopup = LobbyPopup(context, true) { createSeed() }
            }
        }
        _state.value = State.PreGame
    }

    private fun createSeed() {
        if (::server.isInitialized) {
            Random.nextLong().let { seed ->

                goFishLogic.updateSeed(seed)
                server.send(seed)
            }
        }
    }

    fun write(player: String, card: Rank) {
        viewModelScope.launch {
            if (::server.isInitialized) {
                server.send(uid?.let { GoFishLogic.Play(it, player, card) })
            }
        }
    }

    fun disconnect() {
        if (::server.isInitialized) {
            server.disconnect()
        }
    }

    //Ui Part
    fun showLobby(data: Boolean, view: View) {
        try {
            if (data) {
                lobbyPopup.showPopup(view)
            } else {
                lobbyPopup.dismissPopup()
            }
        } catch (ex: ExceptionInInitializerError) { }
    }

    fun showEndScreen(view: View, check:Boolean) {
        if(check) {
            goFishLogic.gamePlayers.value?.let {
                EndScreenPopup(
                    application.applicationContext,
                    it
                ).showPopup(view)
            }
        }
    }
}