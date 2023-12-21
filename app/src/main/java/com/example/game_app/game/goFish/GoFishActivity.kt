package com.example.game_app.game.goFish

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import  android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.game_app.R
import com.example.game_app.common.itemClickListener
import com.example.game_app.data.LobbyInfo
import com.example.game_app.databinding.ActivityGoFishBinding
import com.example.game_app.game.Card
import com.example.game_app.game.Rank
import com.example.game_app.game.goFish.popup.PopupLobby
import com.example.game_app.game.goFish.popup.PopupPickCard
import java.util.Timer
import java.util.TimerTask

class GoFishActivity : AppCompatActivity() {
    private val goFishViewModel: GoFishViewModel by viewModels()
    private lateinit var goFishLogic: GoFishLogic
    private lateinit var binding: ActivityGoFishBinding
    private var serverUid: String? = null
    private var yourDeck :List<Card> = mutableListOf()
    private var elseDecks: List<GoFishLogic.Player> = mutableListOf()
    private var isYourTurn = false

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goFishLogic = goFishViewModel.goFishLogic
        binding = ActivityGoFishBinding.inflate(layoutInflater)
        setContentView(binding.root)
        serverUid = intent.getStringExtra("lobbyUid")
        val cardViewAdapter =  CardsRecycleView()
        val playerViewAdapter =  PlayersRecycleView().apply {
            itemClickListener = object : itemClickListener<GoFishLogic.Player> {
                override fun onItemClicked(item: GoFishLogic.Player, itemPosition: Int) {
                    PopupPickCard(applicationContext).apply {
                        item.let { player ->
                            if (isYourTurn) {
                                showPopup(findViewById(android.R.id.content), player.info, yourDeck)
                                adapter.itemClickListener = object : itemClickListener<Rank> {
                                    override fun onItemClicked(item: Rank, itemPosition: Int) {
                                        dismiss()
                                        goFishViewModel.write(
                                            Play(goFishViewModel.sharedAccount!!,
                                                player.info.uid,
                                                item))
                                    }
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
            if (!players.isNullOrEmpty()) {
                yourDeck = players.find { it.info.uid == goFishViewModel.sharedAccount }?.deck?: mutableListOf()
                cardViewAdapter.updateItems(yourDeck)
                elseDecks = players.filter { it.info.uid != goFishViewModel.sharedAccount }
                playerViewAdapter.updateItems(elseDecks)
                findViewById<TextView>(R.id.deckSize).text = goFishLogic.getDeckSize().toString()
            }
        }
        goFishLogic.playerToTakeTurn.observe(this){turn->
            isYourTurn = (turn.info.uid === goFishViewModel.sharedAccount)
             findViewById<TextView>(R.id.playerTurn).apply{
                 text = "It's ${turn.info.username}'s turn"
                 visibility = View.VISIBLE
                 Timer().schedule(object : TimerTask(){
                     override fun run() { runOnUiThread{visibility = View.GONE} }
                 },1000)
             }
        }
        if (!serverUid.isNullOrEmpty()) {
            goFishViewModel.joinGame(serverUid!!)
            findViewById<View>(android.R.id.content).post {
                PopupLobby(this).showPopup(findViewById(android.R.id.content))
            }
        } else {
            goFishViewModel.createGame(
                LobbyInfo(
                    lobbyName = "text",
                    maxPlayerCount = 4,
                    gamemode = "goFish",
                    gamemodeId = 0,
                    connection = "local"
                )
            )
            findViewById<View>(android.R.id.content).post {
                PopupLobby(this) { goFishViewModel.startGame() }.showPopup(findViewById(android.R.id.content))
            }
        }

    }
}