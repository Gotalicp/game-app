package com.example.game_app.domain.game.chess

import com.example.game_app.R
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side

enum class ChessPieces(val symbol: Piece,val side: Side, val image: Int?) {
    EMPTY(Piece.NONE, Side.WHITE, null),
    WHITE_KING(Piece.WHITE_KING, Side.WHITE, R.drawable.white_king),
    WHITE_QUEEN(Piece.WHITE_QUEEN, Side.WHITE, R.drawable.white_queen),
    WHITE_ROOK(Piece.WHITE_ROOK, Side.WHITE, R.drawable.white_rook),
    WHITE_BISHOP(Piece.WHITE_BISHOP, Side.WHITE, R.drawable.white_bishop),
    WHITE_KNIGHT(Piece.WHITE_KNIGHT, Side.WHITE, R.drawable.white_horse),
    WHITE_PAWN(Piece.WHITE_PAWN, Side.WHITE, R.drawable.white_pawn),
    BLACK_KING(Piece.BLACK_KING, Side.BLACK, R.drawable.black_king),
    BLACK_QUEEN(Piece.BLACK_QUEEN, Side.BLACK, R.drawable.black_queen),
    BLACK_ROOK(Piece.BLACK_ROOK, Side.BLACK, R.drawable.black_rook),
    BLACK_BISHOP(Piece.BLACK_BISHOP, Side.BLACK, R.drawable.black_bishop),
    BLACK_KNIGHT(Piece.BLACK_KNIGHT, Side.BLACK, R.drawable.black_horse),
    BLACK_PAWN(Piece.BLACK_PAWN, Side.BLACK, R.drawable.black_pawn)
}