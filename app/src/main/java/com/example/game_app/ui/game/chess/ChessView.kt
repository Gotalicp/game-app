package com.example.game_app.ui.game.chess

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.game_app.domain.game.chess.ChessDelegate
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.PieceType
import com.github.bhlangonijr.chesslib.Side
import kotlin.math.min

class ChessView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val scaleFactor = 1.0f
    private var originX = 20f
    private var originY = 200f
    private var cellSide = 130f

    enum class GridColor(private val color: Int) {
        LightColor(Color.parseColor("#EEEEEE")),
        DarkColor(Color.parseColor("#BBBBBB")),
        GreenColor(Color.GREEN),
        RedColor(Color.RED);

        fun getColor(): Int {
            return color
        }
    }

    private val paint = Paint()

    private var movingPieceBitmap: Drawable? = null
    private var movingPieceType: PieceType? = null
    private var movingPiece: String? = null

    var chessDelegate: ChessDelegate? = null
    var side: Side? = null

    private var fromCol: Int = -1
    private var fromRow: Int = -1
    private var movingPieceX = -1f
    private var movingPieceY = -1f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        min(widthMeasureSpec, heightMeasureSpec).let {
            setMeasuredDimension(it, it)
        }
    }

    override fun onDraw(canvas: Canvas) {
        (min(width, height) * scaleFactor).let {
            cellSide = it / 8f
            originX = (width - it) / 2f
            originY = (height - it) / 2f
        }
        drawChessboard(canvas)
        drawPieces(canvas)
    }

    @SuppressLint("ClickableViewAccessibility", "UseCompatLoadingForDrawables")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                fromCol = ((event.x - originX) / cellSide).toInt()
                fromRow = ((event.y - originY) / cellSide).toInt()
                getCell(fromCol, fromRow).let { cell ->
                    chessDelegate?.pieceAt(cell)?.let {
                        if (it.side == side) {
                            movingPiece = cell
                            movingPieceType = it.pieceType
                            movingPieceBitmap =
                                it.image?.let { image ->
                                    context.resources.getDrawable(
                                        image,
                                        null
                                    )
                                }
                        } else {
                            return false
                        }
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                movingPieceX = event.x.coerceIn(originX + cellSide / 2, originX + 7.5f * cellSide)
                movingPieceY = event.y.coerceIn(originY + cellSide / 2, originY + 7.5f * cellSide)
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                val col = ((event.x - originX) / cellSide).toInt()
                val row = ((event.y - originY) / cellSide).toInt()
                if (fromCol != col || fromRow != row) {
                    if (event.x in originX..(originX + 8 * cellSide)
                        && event.y in originY..(originY + 8 * cellSide)
                    ) {
                        side?.let {
                            chessDelegate?.movePiece(
                                it,
                                Pair(
                                    getCell(fromCol, fromRow),
                                    getCell(col, row)
                                ),
                                (row <= 0 && movingPieceType == PieceType.PAWN)
                            )
                        }
                    }
                }
                movingPiece = null
                movingPieceType = null
                movingPieceBitmap = null
                invalidate()
            }
        }
        return true
    }

    private fun drawPieces(canvas: Canvas) {
        for (row in 0 until 8)
            for (col in 0 until 8)
                chessDelegate?.pieceAt(getCell(col, row))?.let {
                    if (getCell(col, row) != movingPiece) {
                        it.image?.let { it1 -> drawPieceAt(canvas, col, row, it1) }
                    }
                }

        movingPieceBitmap?.let { drawable ->
            drawable.setBounds(
                (movingPieceX - cellSide / 2).toInt(),
                (movingPieceY - cellSide / 2).toInt(),
                (movingPieceX + cellSide / 2).toInt(),
                (movingPieceY + cellSide / 2).toInt()
            )
            drawable.draw(canvas)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun drawPieceAt(canvas: Canvas, col: Int, row: Int, image: Int) {
        context.resources.getDrawable(image, null).apply {
            setBounds(
                (originX + col * cellSide).toInt(),
                (originY + row * cellSide).toInt(),
                (originX + (col + 1) * cellSide).toInt(),
                (originY + (row + 1) * cellSide).toInt()
            )
            draw(canvas)
        }
    }

    private fun getCell(col: Int, row: Int) = if (side == Side.WHITE) {
        "${('A' + col)}${8 - row}"
    } else {
        "${('H' - col)}${1 + row}"
    }

    //Draws board
    private fun drawChessboard(canvas: Canvas) {
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                drawSquareAt(
                    canvas,
                    col,
                    row,
                    movingPiece?.let { cell ->
                        chessDelegate?.getLegalMoves(cell)
                            ?.find { it.value() == getCell(col, row) }
                            ?.let { GridColor.GreenColor }
                    } ?: if ((col + row) % 2 == 1) GridColor.DarkColor else GridColor.LightColor,
                    getCell(col, row)
                )
            }
        }
    }

    private fun drawSquareAt(canvas: Canvas, col: Int, row: Int, color: GridColor, text: String) {
        paint.color = color.getColor()
        canvas.drawRect(
            originX + col * cellSide,
            originY + row * cellSide,
            originX + (col + 1) * cellSide,
            originY + (row + 1) * cellSide,
            paint
        )
        //Show tile name for debugging
        paint.color = Color.BLACK
        paint.textSize = 24f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(
            text,
            originX + (col + 0.5f) * cellSide,
            (originY + (row + 0.5f) * cellSide),
            paint
        )
    }
}
