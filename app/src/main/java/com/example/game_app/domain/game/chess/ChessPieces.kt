package com.example.game_app.domain.game.chess

import com.example.game_app.R
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.PieceType
import com.github.bhlangonijr.chesslib.Side

enum class ChessPieces(val symbol: Piece,val side: Side, val image: Int?, val pieceType: PieceType) {
    EMPTY(Piece.NONE, Side.WHITE, null,PieceType.NONE),
    WHITE_KING(Piece.WHITE_KING, Side.WHITE, R.drawable.white_king,PieceType.KING),
    WHITE_QUEEN(Piece.WHITE_QUEEN, Side.WHITE, R.drawable.white_queen,PieceType.QUEEN),
    WHITE_ROOK(Piece.WHITE_ROOK, Side.WHITE, R.drawable.white_rook,PieceType.ROOK),
    WHITE_BISHOP(Piece.WHITE_BISHOP, Side.WHITE, R.drawable.white_bishop,PieceType.BISHOP),
    WHITE_KNIGHT(Piece.WHITE_KNIGHT, Side.WHITE, R.drawable.white_horse,PieceType.KNIGHT),
    WHITE_PAWN(Piece.WHITE_PAWN, Side.WHITE, R.drawable.white_pawn,PieceType.PAWN),
    BLACK_KING(Piece.BLACK_KING, Side.BLACK, R.drawable.black_king,PieceType.KING),
    BLACK_QUEEN(Piece.BLACK_QUEEN, Side.BLACK, R.drawable.black_queen,PieceType.QUEEN),
    BLACK_ROOK(Piece.BLACK_ROOK, Side.BLACK, R.drawable.black_rook,PieceType.ROOK),
    BLACK_BISHOP(Piece.BLACK_BISHOP, Side.BLACK, R.drawable.black_bishop,PieceType.BISHOP),
    BLACK_KNIGHT(Piece.BLACK_KNIGHT, Side.BLACK, R.drawable.black_horse,PieceType.KNIGHT),
    BLACK_PAWN(Piece.BLACK_PAWN, Side.BLACK, R.drawable.black_pawn,PieceType.PAWN)
}