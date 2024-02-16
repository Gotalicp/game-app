package com.example.game_app.ui.game.goFish

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.game_app.R
import com.example.game_app.ui.common.AppAcc
import com.example.game_app.data.PlayerCache
import com.example.game_app.domain.SharedInformation
import com.example.game_app.databinding.ActivityGoFishBinding
import com.example.game_app.domain.game.GoFishLogic
import com.example.game_app.domain.game.Rank
import com.example.game_app.domain.server.OkClient
import com.example.game_app.domain.server.OkServer
import com.example.game_app.domain.server.ServerInterface
import com.example.game_app.ui.game.DrawingCardAnimation
import com.example.game_app.ui.game.GivingCardAnimation
import com.example.game_app.ui.game.goFish.popup.EndScreenPopup
import com.example.game_app.ui.game.goFish.popup.LobbyPopup
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
    private var timer: Long? = null

    var uid = SharedInformation.getAcc().value?.uid
    private var lobby = SharedInformation.getLobby()
    private var cache = PlayerCache.instance
    var goFishLogic = GoFishLogic()

    //PopUps
    private lateinit var lobbyPopup: LobbyPopup

    val id = SharedInformation.getAcc().value?.uid

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
        Log.d("GoFishViewModel", "Player To Take Turn: $player")
        _state.postValue(cache.get(player)?.username?.let { name ->
            State.MyTurn((player == uid), name, View.VISIBLE)
        })
        delay(3000L)
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
        lobby.value?.let { lobby ->
            players = lobby.players.map {
                PlayerCache.instance.get(it)!!
            }.toList()
            rounds = lobby.rounds
            try {
                timer = lobby.secPerTurn.toLong() * 1000
            } catch (_: Exception) {
            }
        }
    }

    fun findPlayers() =
        goFishLogic.gamePlayers.value?.associateBy { it.uid }?.let { gamePlayersMap ->
            players?.mapNotNull { player ->
                gamePlayersMap[player.uid]?.let { gamePlayer ->
                    Pair(gamePlayer.deck, player)
                }
            }
        }?.partition { it.second.uid == uid }

    fun findMyDeck() =
        goFishLogic.gamePlayers.value?.find { uid == it.uid }?.deck?.sortedBy { it.rank }

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

    @SuppressLint("DiscouragedApi", "SetTextI18n")
    fun showAnimation(
        plays: MutableList<Pair<GoFishLogic.Play, Int>>,
        binding: ActivityGoFishBinding
    ) {
        val idImage = ""
        binding.apply {
            if (plays.isNotEmpty()) {
                plays.last().let {
                    val view1 = if (it.first.askingPlayer != uid) {
                        getPositionById(playerView, it.first.askingPlayer)
                    } else {
                        profile
                    }
                    if (it.second != 0) {
                        val view2 =
                            if (it.first.askedPlayer != uid) {
                                getPositionById(playerView, it.first.askedPlayer)
                            } else {
                                profile
                            }
                        try {
                            numberCards.text = "${it.second}x"
                            imageCard.setImageDrawable(
                                ContextCompat.getDrawable(
                                    root.context, root.context.resources.getIdentifier(
                                        idImage,
                                        "drawable",
                                        root.context.packageName
                                    )
                                )
                            )
                            if (view1 != null && view2 != null)
                                givingCardAnimation(view2, view1, binding)
                        } catch (_: Exception) {
                        }
                    } else {
                        numberCards.text = ""
                        imageCard.setImageResource(R.drawable.back)
                        if (view1 != null)
                            drawingCardAnimation(view1, binding)
                    }
                }
            }
        }
    }

    private fun getPositionById(recyclerView: RecyclerView, id: String): View? {
        val itemCount = recyclerView.adapter?.itemCount ?: 0
        for (i in 0 until itemCount) {
            (recyclerView.findViewHolderForAdapterPosition(i) as? PlayersRecycleView.PlayersViewHolder)?.let {
                if (it.id == id) {
                    return it.itemView
                }
            }
        }
        return null
    }

    fun setTimer(binding: ActivityGoFishBinding, uid: String) {
        timer?.let {
            callTimer(getPositionById(binding.playerView, uid)?.findViewById(R.id.timeTurn) ?:binding.timeTurn)
        }
    }

    private var countDown: CountDownTimer? = null
    private fun callTimer(progress: ProgressBar) {
        countDown?.cancel()
        progress.visibility = View.VISIBLE
        countDown = object : CountDownTimer(timer!!, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                progress.progress = (millisUntilFinished / 1000).toInt()
            }
            override fun onFinish() {
                Log.d("tick", "pog2")
                progress.progress = 0
                progress.visibility = View.GONE
                goFishLogic.skipPlayer()
            }
        }
        countDown?.start()
    }

    private fun givingCardAnimation(
        view1: View,
        view2: View,
        binding: ActivityGoFishBinding
    ) {
        binding.movableCard.apply {
            visibility = View.VISIBLE
            val coordinates1 = IntArray(2)
            view1.getLocationOnScreen(coordinates1)
            val coordinates2 = IntArray(2)
            view2.getLocationOnScreen(coordinates2)
            startAnimation(
                GivingCardAnimation(
                    this,
                    coordinates1[0].toFloat(),
                    coordinates1[1].toFloat(),
                    coordinates2[0].toFloat(),
                    coordinates2[1].toFloat()
                ).apply {
                    duration = 1000
                })
        }
    }

    private fun drawingCardAnimation(
        view: View,
        binding: ActivityGoFishBinding
    ) {
        binding.movableCard.apply {
            visibility = View.VISIBLE
            val coordinates1 = IntArray(2)
            view.getLocationOnScreen(coordinates1)
            startAnimation(
                DrawingCardAnimation(
                    this,
                    coordinates1[0].toFloat(),
                    coordinates1[1].toFloat(),
                ).apply {
                    duration = 1000
                })
        }
    }
}