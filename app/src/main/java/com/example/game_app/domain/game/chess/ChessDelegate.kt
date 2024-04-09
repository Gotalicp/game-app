package com.example.game_app.domain.game.chess

import com.github.bhlangonijr.chesslib.Square

interface ChessDelegate {
    fun pieceAt(pawn: String) : ChessPieces?
    fun movePiece(move: Pair<String,String>)
    fun getLegalMoves(location:String): List<Square>
}