package com.example.game_app.ui.game.goFish

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.map
import com.example.game_app.R
import com.example.game_app.data.common.itemClickListener
import com.example.game_app.databinding.ActivityGoFishBinding
import com.example.game_app.domain.Rank
import com.example.game_app.ui.game.goFish.popup.PopupLobby
import com.example.game_app.ui.game.goFish.popup.PopupPickCard
import java.util.Timer
import java.util.TimerTask

class GoFishActivity : AppCompatActivity() {
    private val goFishViewModel: GoFishViewModel by viewModels()
    private lateinit var goFishLogic: GoFishLogic
    private lateinit var binding: ActivityGoFishBinding
    private lateinit var lobbyPopup: PopupLobby

    private val cardViewAdapter = CardsRecycleView()
    private val playerViewAdapter = PlayersRecycleView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goFishLogic = goFishViewModel.goFishLogic
        binding = ActivityGoFishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goFishViewModel.state.map { GoFishUiMapper.map(it) }.observe(this) { updateContent(it) }

        intent.getStringExtra("lobbyUid")?.let { uid ->
            intent.getStringExtra("lobbyIp")?.let {
                goFishViewModel.joinGame(uid, it)
                findViewById<View>(android.R.id.content).post {
                    lobbyPopup = PopupLobby(this)
                    goFishViewModel.showLobby()
                }
            }
        } ?: run {
            goFishViewModel.createGame()
            findViewById<View>(android.R.id.content).post {
                lobbyPopup = PopupLobby(this) { goFishViewModel.startGame() }
                goFishViewModel.showLobby()
            }
        }

        playerViewAdapter.apply {
            itemClickListener = object : itemClickListener<GoFishLogic.Player> {
                override fun onItemClicked(
                    item: GoFishLogic.Player, itemPosition: Int
                ) {
                    PopupPickCard(applicationContext).apply {
                        item.let { player ->
                            showPopup(
                                findViewById(android.R.id.content), player.info, item.deck

                            )
                            adapter.itemClickListener = object : itemClickListener<Rank> {
                                override fun onItemClicked(item: Rank, itemPosition: Int) {
                                    dismiss()
                                    goFishViewModel.write(
                                        Play(
                                            goFishViewModel.uid!!, player.info.uid, item
                                        )
                                    )
                                }
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

        goFishLogic.gamePlayers.observe(this) { players ->
            players.partition { it.info.uid == goFishViewModel.uid }.let {
                playerViewAdapter.updateItems(it.second)
                cardViewAdapter.updateItems(it.first.first().deck)
            }
            findViewById<TextView>(R.id.deckSize).text = goFishLogic.getDeckSize().toString()
        }

        goFishLogic.playerToTakeTurn.observe(this) { turn ->
            findViewById<TextView>(R.id.playerTurn).apply {
                text = "It's ${turn.info.username}'s turn"
                visibility = View.VISIBLE
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        runOnUiThread { visibility = View.GONE }
                    }
                }, 3000)
            }
        }
    }

    private fun updateContent(data: GoFishUiModel) {
            if (data.showLobby) {
                lobbyPopup.showPopup(findViewById(android.R.id.content))
            } else {
                lobbyPopup.dismissPopup()
            }
            playerViewAdapter.isYourTurn = data.isYourTurn
    }
}