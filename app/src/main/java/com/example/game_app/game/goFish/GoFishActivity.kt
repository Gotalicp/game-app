package com.example.game_app.game.goFish

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.game_app.data.LobbyInfo
import com.example.game_app.databinding.ActivityGoFishBinding
import com.example.game_app.game.goFish.popup.PopupLobby

class GoFishActivity : AppCompatActivity() {
    private val goFishViewMode: GoFishViewModel by viewModels()
    private lateinit var binding: ActivityGoFishBinding
    private var uid: String? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoFishBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var playerViewAdapter =  PlayersRecycleView()
        var cardViewAdapter =  CardsRecycleView()
        binding.apply {
            cardView.adapter = cardViewAdapter
            playerView.adapter = playerViewAdapter
        }
        goFishViewMode.goFishLogic.gamePlayers.observe(this) { players ->
            if (!players.isNullOrEmpty()) {
                cardViewAdapter.updateItems(players.find { it.info.uid == goFishViewMode.sharedAccount }?.deck?: mutableListOf())
                playerViewAdapter.updateItems(players.filter { it.info.uid != goFishViewMode.sharedAccount })
            }
        }

        if (!uid.isNullOrEmpty()) {
            goFishViewMode.joinGame(uid!!)
            findViewById<View>(android.R.id.content).post {
                PopupLobby(this) {}.showPopup(findViewById(android.R.id.content))
            }
        } else {
            goFishViewMode.createGame(
                LobbyInfo(
                    lobbyName = "text",
                    maxPlayerCount = 4,
                    gamemode = "gofish",
                    gamemodeId = 0,
                    connection = "local"
                )
            )
            findViewById<View>(android.R.id.content).post {
                PopupLobby(this) { goFishViewMode.startGame() }.showPopup(findViewById(android.R.id.content))
            }
        }

    }
}