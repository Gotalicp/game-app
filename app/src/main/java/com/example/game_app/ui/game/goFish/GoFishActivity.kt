package com.example.game_app.ui.game.goFish

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.map
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.game_app.R
import com.example.game_app.data.SharedTheme
import com.example.game_app.databinding.ActivityGoFishBinding
import com.example.game_app.domain.game.Rank
import com.example.game_app.ui.common.AppAcc
import com.example.game_app.ui.common.ItemClickListener
import com.example.game_app.ui.game.GameUiMapper
import com.example.game_app.ui.game.GameUiModel
import com.example.game_app.ui.game.dialogs.StartingInDialogFragment
import com.example.game_app.ui.game.dialogs.end.EndDialogFragment
import com.example.game_app.ui.game.dialogs.lobby.LobbyDialogFragment
import com.example.game_app.ui.game.goFish.popup.CardPickerPopup

class GoFishActivity : AppCompatActivity() {
    private val viewModel: GoFishViewModel by viewModels()
    private lateinit var binding: ActivityGoFishBinding

    private val cardViewAdapter = CardsRecycleView()
    private val playerViewAdapter = PlayersRecycleView()

    private var lobby: LobbyDialogFragment? = null

    @SuppressLint("DiscouragedApi", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoFishBinding.inflate(layoutInflater)
        setTheme(SharedTheme(this).getTheme())
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

        playerViewAdapter.apply {

            itemClickListener = object : ItemClickListener<AppAcc> {
                override fun onItemClicked(item: AppAcc, itemPosition: Int) {
                    CardPickerPopup(application).apply {
                        viewModel.findMyDeck()?.let {
                            showPopup(binding.root, item, it)
                        }
                        adapter.itemClickListener = object : ItemClickListener<Rank> {
                            override fun onItemClicked(card: Rank, itemPosition: Int) {
                                dismiss()
                                item.uid.let { viewModel.write(it, card) }
                            }
                        }
                    }
                }
            }
        }
        binding.apply {
            cardView.adapter = cardViewAdapter
            playerView.adapter = playerViewAdapter
            if (playerView.itemAnimator is SimpleItemAnimator) {
                (playerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }

            viewModel.goFishLogic.play.observe(this@GoFishActivity) { plays ->
                viewModel.showAnimation(
                    plays, this,
                    playerView.findViewWithTag(plays.first.askingPlayer) ?: profile,
                    playerView.findViewWithTag(plays.first.askedPlayer) ?: profile
                )
            }

            viewModel.goFishLogic.gamePlayers.observe(this@GoFishActivity) {
                profile.visibility = View.VISIBLE
                viewModel.findMyDeck()?.let { cardViewAdapter.updateItems(it) }
                viewModel.findPlayers()?.let { players ->
                    playerViewAdapter.updateItems(players.second)
                    players.first.first().let { me ->
                        yourImage.setImageBitmap(me.second.image)
                        yourName.text = "${me.first.player.score}: ${me.second.username}"
                    }
                }
                deckSize.text = "${viewModel.goFishLogic.getDeckSize()}"
            }
        }
        viewModel.state.map { GameUiMapper.map(it) }.observe(this) { updateContent(it) }
    }

    @SuppressLint("SetTextI18n")
    private fun updateContent(data: GameUiModel) {
        binding.apply {
            data.startingIn?.let {
                StartingInDialogFragment(it).show(
                    supportFragmentManager,
                    StartingInDialogFragment.TAG
                )
            }
            data.host?.let {
                lobby = LobbyDialogFragment(
                    it,
                    viewModel.createSeed,
                    listOf(2, 3, 4, 5, 6),
                    listOf("No limit", "15", "30", "45", "60"),
                    listOf(1, 2, 3, 4, 5)
                )
            }
            if (data.showLobby) {
                lobby?.show(supportFragmentManager, LobbyDialogFragment.TAG)
            } else {
                lobby?.dismiss()
            }
            if (data.showEnd) {
                viewModel.getFinalScores()?.let {
                    EndDialogFragment(it).show(
                        supportFragmentManager,
                        EndDialogFragment.TAG
                    )
                }
            }
            playerViewAdapter.isYourTurn = data.isYourTurn
            data.playerUid?.let {
                viewModel.setTimer(
                    playerView.findViewWithTag<View?>(it)?.findViewById(R.id.timeTurn) ?: timeTurn,
                    it
                )
            }
            data.playerName?.let { viewModel.showPlayerToTakeTurn(binding.playerTurn, it) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disconnect()
    }
}