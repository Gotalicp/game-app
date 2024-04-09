package com.example.game_app.domain.game.chess

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.game_app.domain.LobbyProvider
import com.example.game_app.domain.game.GameLogic
import com.example.game_app.domain.game.PlayerWrapper
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.PieceType
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.Serializable

class ChessLogic : GameLogic<ChessLogic.Play> {
    data class Play(
        val side: Side,
        val from: String,
        val to: String,
        val promotion: String?
    ) : Serializable

    data class Player(
        var side: Side,
        val player: PlayerWrapper
    ) : Serializable

    //All the players
    private val _gamePlayers = MutableLiveData<MutableList<Player>>()
    val gamePlayers: LiveData<MutableList<Player>> get() = _gamePlayers

    private val _playerToTakeTurn = MutableSharedFlow<String>()
    override val playerToTakeTurn: Flow<String?> get() = _playerToTakeTurn

    private val _hasEnded = MutableStateFlow(false)
    override val hasEnded: Flow<Boolean> get() = _hasEnded

    private val _seed = MutableStateFlow<Long?>(null)
    override val seed: Flow<Long?> get() = _seed

    private val _play = MutableLiveData<MutableList<Play>>()
    val play: LiveData<MutableList<Play>> get() = _play

    private var board = Board()

    override suspend fun startGame(seed: Long) {
        if (_gamePlayers.value == null) {
            LobbyProvider.getLobby().value?.players?.let { setPlayer(it) }
        }
        _hasEnded.value = false
        board = Board()
        switchSides()
        _gamePlayers.value?.find { it.side == Side.WHITE }?.player?.uid?.let {
            _playerToTakeTurn.emit(it)
        }
    }

    fun getPlayer(uid: String) = _gamePlayers.value?.find { it.player.uid == uid }

    override fun setPlayer(players: MutableList<String>) {
        _gamePlayers.value = mutableListOf(
            Player(Side.WHITE, PlayerWrapper(players.last(), 0)),
            Player(Side.BLACK, PlayerWrapper(players.first(), 0))
        )
    }

    override fun checkEndGame(): Boolean = board.isMated || board.isDraw || board.isStaleMate

    override fun updateSeed(seed: Long) {
        _seed.value = seed
    }

    override suspend fun turnHandling(t: Play) {
        if (board.doMove(convertToMove(t))) {
            nextPlayer(t.side)
            _play.value.let {
                _play.postValue(it?.apply { add(t) } ?: mutableListOf(t))
            }
            if (checkEndGame()) {
                if (board.isMated) {
                    _gamePlayers.value?.find { it.side == t.side }?.player?.let {
                        it.score += 1
                    }
                }
                _hasEnded.value = true
            }
        }
    }

    fun validateMove(play: Play) = (board.legalMoves().find
    { it == convertToMove(play) }?.let
    { true } ?: false)

    private fun convertToMove(t: Play) = t.promotion?.let {
        Move(
            Square.fromValue(t.from),
            Square.fromValue(t.to),
            Piece.make(t.side, PieceType.fromValue(t.promotion))
        )
    } ?: Move(
        Square.fromValue(t.from),
        Square.fromValue(t.to)
    )

    //Sets the next player
    private suspend fun nextPlayer(side: Side) {
        gamePlayers.value?.find { it.side != side }?.let {
            _playerToTakeTurn.emit(it.player.uid)
        }
    }

    private fun switchSides() {
        _gamePlayers.value?.let { players ->
            players.forEach { player ->
                player.side = if (player.side == Side.WHITE) Side.BLACK else Side.WHITE
            }
            _gamePlayers.value = players
        }
    }

    fun getPawnMoves(location: String) =
        board.legalMoves().mapNotNull { if (it.from.toString() == location) it.to else null }

    fun getPawn(location: String): Piece = board.getPiece(Square.fromValue(location))
}