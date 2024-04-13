package com.example.game_app.ui.game.chess

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.map
import com.example.game_app.data.SharedTheme
import com.example.game_app.databinding.ActivityChessBinding
import com.example.game_app.domain.game.chess.ChessDelegate
import com.example.game_app.ui.game.GameUiMapper
import com.example.game_app.ui.game.GameUiModel
import com.example.game_app.ui.game.dialogs.StartingInDialogFragment
import com.example.game_app.ui.game.dialogs.end.EndDialogFragment
import com.example.game_app.ui.game.dialogs.lobby.LobbyDialogFragment
import com.github.bhlangonijr.chesslib.Side

class ChessActivity : AppCompatActivity() {
    private val viewModel: ChessViewModel by viewModels()
    private lateinit var binding: ActivityChessBinding

    private var lobby: LobbyDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(SharedTheme(this).getTheme())
        binding = ActivityChessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("handleOnBackPressed", "Clicked")
            }
        })

        binding.root.post {
            viewModel.joinGame(
                code = intent.getStringExtra("code"),
                uid = intent.getStringExtra("lobbyUid"),
                ip = intent.getStringExtra("lobbyIp")
            )
        }

        binding.apply {
            board.chessDelegate = object : ChessDelegate {
                override fun pieceAt(pawn: String) = viewModel.getChessPiece(pawn)

                override fun movePiece(side: Side, move: Pair<String, String>, promotion: Boolean) {
                    if (promotion) {
                        PromotionDialogFragment(side) {
                            viewModel.validateMove(side,move, it)
                        }.show(supportFragmentManager, "PromotionDialogFragment")
                    } else {
                        viewModel.validateMove(side,move, null)
                    }
                }

                override fun getLegalMoves(location: String) = viewModel.getLegalMoves(location)
            }
            viewModel.play.observe(this@ChessActivity) {
                board.invalidate()
            }
        }

        viewModel.state.map { GameUiMapper.map(it) }.observe(this) { updateContent(it) }
    }

    @SuppressLint("SetTextI18n")
    private fun updateContent(data: GameUiModel) {
        binding.apply {
            data.startingIn?.let { time ->
                StartingInDialogFragment(time).show(
                    supportFragmentManager,
                    StartingInDialogFragment.TAG
                )
                viewModel.getOtherInfo()?.let {
                    yourImage1.setImageBitmap(it.image)
                    yourName1.text = it.username
                }
                viewModel.getMyInfo()?.let {
                    yourImage2.setImageBitmap(it.image)
                    yourName2.text = it.username
                }
                board.side = viewModel.getMySide()
            }
            data.host?.let {
                lobby = LobbyDialogFragment(
                    it,
                    viewModel.createSeed,
                    listOf(2),
                    listOf("No limit", "1", "3", "5", "10"),
                    listOf(1, 3, 5)
                )
            }
            data.showLobby.let {
                if (it) {
                    lobby?.show(supportFragmentManager, LobbyDialogFragment.TAG)
                } else {
                    lobby?.dismiss()
                }
            }
            data.showEnd.let {
                if (it) {
                    viewModel.getFinalScores()?.let { it1 ->
                        EndDialogFragment(it1).show(
                            supportFragmentManager,
                            EndDialogFragment.TAG
                        )
                    }
                }
            }
//            data.playerUid?.let { it1 -> goFishViewModel.setTimer(binding, it1) }
//            data.playerName?.let { goFishViewModel.showPlayerToTakeTurn(binding.playerTurn, it) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disconnect()
    }
}