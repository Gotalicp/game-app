package com.example.game_app.domain.game.chess

import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square

interface ChessDelegate {
    fun pieceAt(pawn: String) : ChessPieces?
    fun movePiece(side: Side, move: Pair<String,String>, promotion: Boolean)
    fun getLegalMoves(location:String): List<Square>
}