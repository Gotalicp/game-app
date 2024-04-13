package com.example.game_app.ui.game.chess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.game_app.databinding.PromotionDialogBinding
import com.example.game_app.domain.game.chess.ChessType
import com.github.bhlangonijr.chesslib.PieceType
import com.github.bhlangonijr.chesslib.Side

class PromotionDialogFragment(
    private val side: Side,
    private val call: (PieceType) -> Unit
) : DialogFragment() {

    private var _binding: PromotionDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PromotionDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            queenButton.setImageResource(ChessType.getImageResourceForSide(ChessType.QUEEN, side))
            rookButton.setImageResource(ChessType.getImageResourceForSide(ChessType.ROOK, side))
            bishopButton.setImageResource(ChessType.getImageResourceForSide(ChessType.BISHOP, side))
            knightButton.setImageResource(ChessType.getImageResourceForSide(ChessType.KNIGHT, side))

            queenButton.setOnClickListener {
                call(PieceType.QUEEN)
                dismiss()
            }

            rookButton.setOnClickListener {
                call.invoke(PieceType.ROOK)
                dismiss()
            }

            bishopButton.setOnClickListener {
                call.invoke(PieceType.BISHOP)
                dismiss()
            }

            knightButton.setOnClickListener {
                call.invoke(PieceType.KNIGHT)
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}