package com.example.game_app.domain.game.chess

import android.util.Log
import com.example.game_app.domain.game.GameLogic
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.move.Move
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class ChessLogic : GameLogic<Move> {

    private val _playerToTakeTurn = MutableSharedFlow<String>()
    override val playerToTakeTurn: Flow<String?> get() = _playerToTakeTurn

    private val _hasEnded = MutableStateFlow(false)
    override val hasEnded: Flow<Boolean> get() = _hasEnded

    private val _seed = MutableStateFlow<Long?>(null)
    override val seed: Flow<Long?> get() = _seed

    private var board = Board()

    init {
        Log.d("board", board.fen)
    }

    override suspend fun startGame(seed: Long) {
        board = Board()
    }

    override fun setPlayer(players: MutableList<String>) {
        TODO("Not yet implemented")
    }

    override fun checkEndGame(): Boolean = board.isMated

    override fun updateSeed(seed: Long) {
        _seed.value = seed
    }

    override suspend fun turnHandling(t: Move) {
        board.doMove(t)
    }
}