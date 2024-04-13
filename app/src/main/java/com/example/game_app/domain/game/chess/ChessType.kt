package com.example.game_app.domain.game.chess

import com.example.game_app.R
import com.github.bhlangonijr.chesslib.Side

enum class ChessType(
    val blackImageResource: Int,
    val whiteImageResource: Int,
) {
    QUEEN(
        R.drawable.black_queen,
        R.drawable.white_queen,

        ),
    ROOK( R.drawable.black_rook, R.drawable.white_rook),
    BISHOP(
        R.drawable.black_bishop,
        R.drawable.white_bishop,

        ),
    KNIGHT(
        R.drawable.black_horse,
        R.drawable.white_horse,
    );

    companion object {
        fun getImageResourceForSide(type: ChessType, side: Side): Int {
            return if (side == Side.BLACK) {
                type.blackImageResource
            } else {
                type.whiteImageResource
            }
        }
    }
}