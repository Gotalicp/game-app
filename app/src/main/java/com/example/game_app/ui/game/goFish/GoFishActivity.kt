package com.example.game_app.ui.game.goFish

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.map
import com.example.game_app.data.fishy.Account
import com.example.game_app.ui.common.ItemClickListener
import com.example.game_app.databinding.ActivityGoFishBinding
import com.example.game_app.domain.game.Card
import com.example.game_app.domain.game.Rank
import com.example.game_app.ui.game.goFish.popup.CardPickerPopup

class GoFishActivity : AppCompatActivity() {
    private val goFishViewModel: GoFishViewModel by viewModels()
    private lateinit var binding: ActivityGoFishBinding

    private val cardViewAdapter = CardsRecycleView()
    private val playerViewAdapter = PlayersRecycleView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoFishBinding.inflate(layoutInflater)
        setContentView(binding.root)
        goFishViewModel.state.map { GoFishUiMapper.map(it) }.observe(this) { updateContent(it) }
        binding.root.post {
            goFishViewModel.joinGame(
                intent.getStringExtra("lobbyIp"),
                intent.getStringExtra("lobbyUid"),
                this
            )
        }

        playerViewAdapter.apply {
            itemClickListener = object : ItemClickListener<Pair<MutableList<Card>, Account>> {
                override fun onItemClicked(
                    item: Pair<MutableList<Card>, Account>,
                    itemPosition: Int
                ) {
                    CardPickerPopup(application).apply {
                        goFishViewModel.findMyDeck()?.let {
                            showPopup(binding.root, item.second, it)
                        }
                        adapter.itemClickListener = object : ItemClickListener<Rank> {
                            override fun onItemClicked(card: Rank, itemPosition: Int) {
                                dismiss()
                                item.second.uid?.let { goFishViewModel.write(it, card) }
                            }
                        }
                    }
                }
            }
        }
        binding.apply {
            cardView.adapter = cardViewAdapter
            playerView.adapter = playerViewAdapter
        }

        goFishViewModel.goFishLogic.play.observe(this) { plays ->

        }

        goFishViewModel.goFishLogic.gamePlayers.observe(this) {
            goFishViewModel.findElse()?.let { playerViewAdapter.updateItems(it) }
            goFishViewModel.findMyDeck()?.let { cardViewAdapter.updateItems(it) }
            binding.deckSize.text = "${goFishViewModel.goFishLogic.getDeckSize()}"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateContent(data: GoFishUiModel) {
        binding.apply {
            root.post {
                goFishViewModel.showLobby(data.showLobby,root)
            }
            playerTurn.visibility = data.playerToTakeTurnVisibility
            playerTurn.text = data.playerToTakeTurn
            playerViewAdapter.isYourTurn = data.isYourTurn
            goFishViewModel.showEndScreen(root,data.showScores)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        goFishViewModel.disconnect()
    }
}