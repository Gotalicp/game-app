package com.example.game_app.ui.game.goFish

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.map
import com.example.game_app.R
import com.example.game_app.data.Account
import com.example.game_app.data.PlayerCache
import com.example.game_app.data.common.ItemClickListener
import com.example.game_app.databinding.ActivityGoFishBinding
import com.example.game_app.domain.Rank
import com.example.game_app.ui.game.goFish.popup.PopupEnd
import com.example.game_app.ui.game.goFish.popup.PopupLobby
import com.example.game_app.ui.game.goFish.popup.PopupPickCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

class GoFishActivity : AppCompatActivity() {
    private val goFishViewModel: GoFishViewModel by viewModels()
    private lateinit var binding: ActivityGoFishBinding
    private lateinit var lobbyPopup: PopupLobby

    private val cardViewAdapter = CardsRecycleView()
    private val playerViewAdapter = PlayersRecycleView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoFishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goFishViewModel.state.map { GoFishUiMapper.map(it) }.observe(this) { updateContent(it) }

        intent.getStringExtra("lobbyUid")?.let { uid ->
            intent.getStringExtra("lobbyIp")?.let { ip ->
                goFishViewModel.joinGame(uid, ip)
                findViewById<View>(android.R.id.content).post {
                    lobbyPopup = PopupLobby(this, false)
                    goFishViewModel.showLobby()
                }
            }
        } ?: run {
            goFishViewModel.createGame()
            findViewById<View>(android.R.id.content).post {
                lobbyPopup = PopupLobby(this, true) { goFishViewModel.createSeed() }
                goFishViewModel.showLobby()
            }
        }

        playerViewAdapter.apply {
            itemClickListener = object : ItemClickListener<Pair<GoFishLogic.Player, Account>> {
                override fun onItemClicked(
                    item: Pair<GoFishLogic.Player, Account>, itemPosition: Int
                ) {
                    PopupPickCard(applicationContext).apply {
                        item.let { player ->
                            goFishViewModel.goFishLogic.gamePlayers.value?.find { it.uid == goFishViewModel.uid }?.deck?.let {
                                showPopup(
                                    findViewById(android.R.id.content),
                                    item.second,
                                    it
                                )
                            }
                            adapter.itemClickListener = object : ItemClickListener<Rank> {
                                override fun onItemClicked(item: Rank, itemPosition: Int) {
                                    dismiss()
                                    goFishViewModel.write(
                                        Play(goFishViewModel.uid!!, player.first.uid, item)
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

        goFishViewModel.goFishLogic.gamePlayers.observe(this) { players ->
            players.partition { it.uid == goFishViewModel.uid }.let { player ->
                GlobalScope.launch(Dispatchers.Main) {
                    playerViewAdapter.updateItems(player.second.map {
                        Pair(
                            it,
                            PlayerCache.instance.get(it.uid)!!
                        )
                    })
                }
                cardViewAdapter.updateItems(player.first.first().deck)
            }
            findViewById<TextView>(R.id.deckSize).text =
                "${goFishViewModel.goFishLogic.getDeckSize()}"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateContent(data: GoFishUiModel) {
        if (data.showLobby) {
            lobbyPopup.showPopup(findViewById(android.R.id.content))
        } else {
            if(::lobbyPopup.isInitialized) {
                lobbyPopup.dismissPopup()
            }
        }
        data.playerToTakeTurn?.let {
            binding.playerTurn.apply {
                text = "It's ${it}'s turn"
                visibility = View.VISIBLE
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        runOnUiThread { visibility = View.GONE }
                    }
                }, 3000)
            }
        }
        playerViewAdapter.isYourTurn = data.isYourTurn
        if (data.showScores) {
            goFishViewModel.goFishLogic.gamePlayers.value?.let {
                PopupEnd(
                    this,
                    it
                ).showPopup(findViewById(android.R.id.content))
            }
        }
        binding.cooldown.visibility = if (data.startingIn == 0) {
            View.GONE
        } else {
            View.VISIBLE
        }
        binding.counter.text = "Game Starts in ${data.startingIn}sec"

    }

    override fun onDestroy() {
        super.onDestroy()
        goFishViewModel.disconnect()
    }
}