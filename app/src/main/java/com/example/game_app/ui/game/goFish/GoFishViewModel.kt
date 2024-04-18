package com.example.game_app.ui.game.goFish

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.game_app.R
import com.example.game_app.data.PlayerCache
import com.example.game_app.data.firebase.FireBaseUtilityHistory
import com.example.game_app.databinding.ActivityGoFishBinding
import com.example.game_app.domain.AccountProvider
import com.example.game_app.domain.LobbyProvider
import com.example.game_app.domain.game.GoFishLogic
import com.example.game_app.domain.game.Rank
import com.example.game_app.domain.server.OkClient
import com.example.game_app.domain.server.OkServer
import com.example.game_app.domain.server.ServerInterface
import com.example.game_app.ui.common.AppAcc
import com.example.game_app.ui.common.CountDown
import com.example.game_app.ui.game.DrawingCardAnimation
import com.example.game_app.ui.game.GameStates
import com.example.game_app.ui.game.GivingCardAnimation
import com.example.game_app.ui.game.dialogs.end.EndWrapper
import com.example.game_app.ui.game.dialogs.end.PlayerLeaderBoardAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.random.Random

class GoFishViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableLiveData<GameStates>()
    val state: LiveData<GameStates> = _state

    private var uid = AccountProvider.getUid()
    private var lobby = LobbyProvider.getLobby()
    private var cache = PlayerCache.instance

    //Server
    private var server: ServerInterface<GoFishLogic.Play>? = null

    var goFishLogic = GoFishLogic()
    var players: List<AppAcc>? = null
    private var rounds: Int = -1
    private var timer: Long? = null

    var createSeed: (() -> Unit)? = null


    private var countDown: CountDown? = null

    //Game
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
        countDown?.cancelCountdown()
        cache.get(player)?.username?.let { name ->
            _state.postValue(GameStates.MyTurn((player == uid), player, name))
        }
    }

    private suspend fun collectSeed(seed: Long) {
        if (players == null) {
            setUp()
        }
        Log.d("GoFishViewModel", "Seed: $seed")
        _state.postValue(GameStates.StartingIn(5000L, false))
        delay(5000L)
        goFishLogic.startGame(seed)
    }

    private fun collectHasEnded(hasEnded: Boolean) {
        if (hasEnded) {
            _state.value = GameStates.EndGame
            this.rounds -= 1
            if (this.rounds > 0) {
                createSeed?.invoke()
            } else {
                players?.let { players ->
                    players.associate {
                        Pair(it.uid, goFishLogic.getPlayer(it.uid)?.player?.score ?: 0)
                    }.let { map ->
                        uid?.let { FireBaseUtilityHistory().updateHistory(map, "GoFish", it) }
                    }
                }
            }
        }
    }

    private suspend fun setUp() {
        lobby.value?.let { lobby ->
            players = lobby.players.mapNotNull {
                PlayerCache.instance.get(it)
            }.apply {
                goFishLogic.setPlayer(map { it.uid })
            }
            rounds = lobby.rounds
            try {
                timer = lobby.secPerTurn.toLong() * 1000
            } catch (_: Exception) {
            }
        }
    }

    //Methods to get Information from GameLogic
    fun findPlayers() =
        goFishLogic.gamePlayers.value?.associateBy { it.player.uid }?.let { gamePlayers ->
            players?.mapNotNull { player ->
                gamePlayers[player.uid]?.let {
                    Pair(it, player)
                }
            }
        }?.partition { it.second.uid == uid }

    fun findMyDeck() =
        goFishLogic.gamePlayers.value?.find { uid == it.player.uid }?.deck?.sortedBy { it.rank }

    fun getFinalScores(): List<EndWrapper>? {
        goFishLogic.gamePlayers.value?.map { it.player }?.let { gamePlayer ->
            players?.let { account ->
                return PlayerLeaderBoardAdapter().adapt(Pair(account, gamePlayer))
            }
        }
        return null
    }

    //Server Part
    fun joinGame(code: String? = null, uid: String? = null, ip: String? = null) {
        server = if (code != null && uid != null && ip != null) {
            OkClient(
                gameLogic = goFishLogic,
                expectedTClazz = GoFishLogic.Play::class.java,
                ip = ip,
                code = code,
                port = 8888
            ).apply {
                join()
                _state.value = GameStates.PreGame(false)
            }
        } else {
            OkServer(
                gameLogic = goFishLogic,
                expectedTClazz = GoFishLogic.Play::class.java,
                clazz = "GoFish",
                port = 8888
            ).apply {
                join()
                createSeed = {
                    Random.nextLong().let { seed ->
                        goFishLogic.updateSeed(seed)
                        server?.send(seed)
                    }
                }
                _state.value = GameStates.PreGame(true)
            }
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
    @SuppressLint("DiscouragedApi", "SetTextI18n")
    fun showAnimation(
        plays: Pair<GoFishLogic.Play, Int>,
        binding: ActivityGoFishBinding,
        asking: View,
        asked: View
    ) {
        binding.apply {
            plays.let {
                if (it.second != 0) {
                    numberCards.text = "${it.second}x".uppercase()
                    imageCard.setImageResource(
                        root.context.resources.getIdentifier(
                            it.first.rank.name.lowercase(),
                            "drawable",
                            root.context.packageName
                        )
                    )
                    movableCard.startAnimation(GivingCardAnimation(movableCard, asking, asked))
                } else {
                    numberCards.text = ""
                    imageCard.setImageResource(R.drawable.back)
                    movableCard.startAnimation(DrawingCardAnimation(movableCard, asking))
                }
            }
        }
    }

    fun setTimer(view: ProgressBar, uid: String) {
        if (!players.isNullOrEmpty()) {
            timer?.let {
                countDown = CountDown(
                    view,
                    { viewModelScope.launch { goFishLogic.skipPlayer(uid) } }, it
                )
                countDown?.start()
            }
        }
    }


    fun showPlayerToTakeTurn(view: TextView, data: String) {
        view.text = data
        viewModelScope.launch {
            view.visibility = View.VISIBLE
            delay(1000)
            view.visibility = View.GONE
        }
    }
}