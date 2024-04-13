package com.example.game_app.ui.game.chess

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.game_app.data.PlayerCache
import com.example.game_app.data.firebase.FireBaseUtilityHistory
import com.example.game_app.domain.AccountProvider
import com.example.game_app.domain.LobbyProvider
import com.example.game_app.domain.game.chess.ChessLogic
import com.example.game_app.domain.game.chess.ChessPieces
import com.example.game_app.domain.server.OkClient
import com.example.game_app.domain.server.OkServer
import com.example.game_app.domain.server.ServerInterface
import com.example.game_app.ui.common.AppAcc
import com.example.game_app.ui.game.GameStates
import com.example.game_app.ui.game.dialogs.end.EndScreenAdapter
import com.example.game_app.ui.game.dialogs.end.EndWrapper
import com.example.game_app.ui.game.dialogs.end.PlayerLeaderBoardAdapter
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.PieceType
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.random.Random

class ChessViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableLiveData<GameStates>()
    val state: LiveData<GameStates> = _state

    private var uid = AccountProvider.getUid()
    private var lobby = LobbyProvider.getLobby()
    private var cache = PlayerCache.instance

    private var server: ServerInterface<ChessLogic.Play>? = null

    private var rounds: Int = -1
    private var timer: Long? = null
    private var chessLogic = ChessLogic()
    var players: List<AppAcc>? = null
    var createSeed: (() -> Unit)? = null
    val play = chessLogic.play

    //Game
    init {
        viewModelScope.launch {
            chessLogic.hasEnded.onEach {
                collectHasEnded(it)
            }.launchIn(this)
            chessLogic.seed.onEach {
                it?.let { collectSeed(it) }
            }.launchIn(this)
            chessLogic.playerToTakeTurn.onEach {
                it?.let { collectPlayer(it) }
            }.launchIn(this)
        }
    }

    private suspend fun collectPlayer(player: String) {
        _state.postValue(cache.get(player)?.username?.let { name ->
            GameStates.MyTurn((player == uid), player, name)
        })
    }

    private suspend fun collectSeed(seed: Long) {
        if (players == null) {
            setUp()
        }
        Log.d("Chess", "Seed: $seed")
        _state.postValue(GameStates.StartingIn(5000L, false))
        delay(5000L)
        chessLogic.startGame(seed)
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
                        Pair(it.uid, chessLogic.getPlayer(it.uid)?.player?.score ?: 0)
                    }.let { map ->
                        uid?.let { FireBaseUtilityHistory().updateHistory(map, "Chess", it) }
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
                chessLogic.setPlayer(map { it.uid }.toMutableList())
            }
            rounds = lobby.rounds
            try {
                timer = lobby.secPerTurn.toLong() * 1000
            } catch (_: Exception) {
            }
        }
    }

    fun getMySide() = uid?.let { chessLogic.getPlayer(it)?.side }
    fun getMyInfo() = uid?.let { uid -> players?.find { it.uid == uid } }
    fun getOtherInfo() = uid?.let { uid -> players?.find { it.uid != uid } }
    fun getFinalScores(): List<EndWrapper>? {
        chessLogic.gamePlayers.value?.map { it.player }?.let { gamePlayer ->
            players?.let { account ->
                return PlayerLeaderBoardAdapter().adapt(Pair(account, gamePlayer))
            }
        }
        return null
    }

    //Server part
    fun joinGame(code: String? = null, uid: String? = null, ip: String? = null) {
        server = if (code != null && uid != null && ip != null) {
            OkClient(
                gameLogic = chessLogic,
                expectedTClazz = ChessLogic.Play::class.java,
                ip = ip,
                code = code,
                port = 8880
            ).apply {
                join()
                _state.value = GameStates.PreGame(false)
            }
        } else {
            OkServer(
                gameLogic = chessLogic,
                expectedTClazz = ChessLogic.Play::class.java,
                clazz = "Chess",
                port = 8880
            ).apply {
                join()
                createSeed = {
                    Random.nextLong().let { seed ->
                        chessLogic.updateSeed(seed)
                        server?.send(seed)
                    }
                }
                _state.value = GameStates.PreGame(true)
            }
        }
    }

    private fun write(play: ChessLogic.Play) {
        viewModelScope.launch {
            server?.send(play)
        }
    }

    fun disconnect() {
        server?.disconnect()
        server = null
    }

    //Chess logic
    fun getChessPiece(pawn: String): ChessPieces? {
        chessLogic.getPawn(pawn).let { piece ->
            return ChessPieces.entries.find { it.symbol == piece }
        }
    }

    fun getLegalMoves(pawn: String): List<Square> = chessLogic.getPawnMoves(pawn)

    fun validateMove(side: Side, move: Pair<String, String>, promotion: PieceType?) {
        ChessLogic.Play(side, move.first, move.second, promotion).let {
            Log.d("play", "$it")
            if (chessLogic.validateMove(it)) {
                write(it)
            }
        }
    }
}