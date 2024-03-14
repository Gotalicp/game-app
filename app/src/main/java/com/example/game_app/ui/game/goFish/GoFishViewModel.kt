package com.example.game_app.ui.game.goFish

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.game_app.R
import com.example.game_app.data.firebase.FireBaseUtilityHistory
import com.example.game_app.ui.common.AppAcc
import com.example.game_app.data.PlayerCache
import com.example.game_app.databinding.ActivityGoFishBinding
import com.example.game_app.domain.AccountProvider
import com.example.game_app.domain.LobbyProvider
import com.example.game_app.domain.game.GoFishLogic
import com.example.game_app.domain.game.Rank
import com.example.game_app.domain.server.OkClient
import com.example.game_app.domain.server.OkServer
import com.example.game_app.domain.server.ServerInterface
import com.example.game_app.ui.common.CountDown
import com.example.game_app.ui.game.DrawingCardAnimation
import com.example.game_app.ui.game.GameStates
import com.example.game_app.ui.game.GivingCardAnimation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.random.Random

class GoFishViewModel(application: Application) : AndroidViewModel(application) {
    private val _gameStates = MutableLiveData<GameStates>()
    val gameStates: LiveData<GameStates> = _gameStates

    //Server
    private var server: ServerInterface<GoFishLogic.Play>? = null

    var players: List<AppAcc>? = null
    private var rounds: Int = -1
    private var timer: Long? = null

    var createSeed: (() -> Unit)? = null

    private var uid = AccountProvider.getUid()
    private var lobby = LobbyProvider.getLobby()
    private var cache = PlayerCache.instance
    var goFishLogic = GoFishLogic()

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
        _gameStates.postValue(cache.get(player)?.username?.let { name ->
            GameStates.MyTurn((player == uid), player, name)
        })
    }

    private suspend fun collectSeed(seed: Long) {
        if (players == null) {
            setUp()
        }
        Log.d("GoFishViewModel", "Seed: $seed")
        _gameStates.postValue(GameStates.StartingIn(5000L, false))
        delay(5000L)
        goFishLogic.startGame(seed)
    }

    private fun collectHasEnded(hasEnded: Boolean) {
        if (hasEnded) {
            _gameStates.value = GameStates.EndGame
            this.rounds -= 1
            if (this.rounds > 0) {
                createSeed?.invoke()
            } else {
                players?.let { players ->
                    players.associate {
                        Pair(it.uid, goFishLogic.getPlayer(it.uid)?.score ?: 0)
                    }.let { map ->
                        uid?.let { FireBaseUtilityHistory().updateHistory(map, "GoFish", it) }
                    }
                }
            }
        }
    }

    private suspend fun setUp() {
        lobby.value?.let { lobby ->
            players = lobby.players.map {
                PlayerCache.instance.get(it)!!
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
        goFishLogic.gamePlayers.value?.associateBy { it.uid }?.let { gamePlayers ->
            players?.mapNotNull { player ->
                gamePlayers[player.uid]?.let {
                    Pair(it, player)
                }
            }
        }?.partition { it.second.uid == uid }

    fun findMyDeck() =
        goFishLogic.gamePlayers.value?.find { uid == it.uid }?.deck?.sortedBy { it.rank }

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
                _gameStates.value = GameStates.PreGame(false)
            }
        } else {
            OkServer(
                gameLogic = goFishLogic,
                expectedTClazz = GoFishLogic.Play::class.java,
                port = 8888
            ).apply {
                join()
                createSeed = {
                    Random.nextLong().let { seed ->
                        goFishLogic.updateSeed(seed)
                        server?.send(seed)
                    }
                }
                _gameStates.value = GameStates.PreGame(true)
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
        binding: ActivityGoFishBinding
    ) {
        binding.apply {
            plays.let {
                val view1 =
                    getPositionById(playerView, it.first.askingPlayer)?.itemView ?: profile
                if (it.second != 0) {
                    try {
                        numberCards.text = "${it.second}x"
                        imageCard.setImageDrawable(
                            ContextCompat.getDrawable(
                                root.context, root.context.resources.getIdentifier(
                                    it.first.rank.name.lowercase(),
                                    "drawable",
                                    root.context.packageName
                                )
                            )
                        )
                        movableCard.apply {
                            startAnimation(
                                GivingCardAnimation(
                                    this,
                                    view1,
                                    getPositionById(playerView, it.first.askedPlayer)?.itemView
                                        ?: profile
                                )
                            )
                        }
                    } catch (_: Exception) {
                    }
                } else {
                    numberCards.text = ""
                    imageCard.setImageResource(R.drawable.back)
                    movableCard.apply {
                        startAnimation(DrawingCardAnimation(this, view1))
                    }
                }
            }
        }
    }

    private fun getPositionById(
        recyclerView: RecyclerView,
        id: String
    ): PlayersRecycleView.PlayersViewHolder? {
        for (i in 0 until (recyclerView.adapter?.itemCount ?: 0)) {
            (recyclerView.findViewHolderForAdapterPosition(i) as? PlayersRecycleView.PlayersViewHolder)?.let {
                if (it.id == id) {
                    return it
                }
            }
        }
        return null
    }

    fun setTimer(binding: ActivityGoFishBinding, uid: String) {
        if (!players.isNullOrEmpty()) {
            timer?.let {
                countDown = CountDown(
                    (getPositionById(binding.playerView, uid)?.timer ?: binding.timeTurn),
                    { viewModelScope.launch { goFishLogic.skipPlayer(uid) } },
                    it,
                    1000
                )
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