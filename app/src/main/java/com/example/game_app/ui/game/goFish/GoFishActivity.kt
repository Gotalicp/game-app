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
import com.example.game_app.ui.common.ItemClickListener
import com.example.game_app.databinding.ActivityGoFishBinding
import com.example.game_app.domain.game.Rank
import com.example.game_app.ui.common.AppAcc
import com.example.game_app.ui.game.goFish.popup.CardPickerPopup
import com.example.game_app.ui.game.goFish.popup.LobbyDialogFragment
import com.example.game_app.ui.game.goFish.popup.StartingInDialogFragment

class GoFishActivity : AppCompatActivity() {
    private val goFishViewModel: GoFishViewModel by viewModels()
    private lateinit var binding: ActivityGoFishBinding

    private val cardViewAdapter = CardsRecycleView()
    private val playerViewAdapter = PlayersRecycleView()

    @SuppressLint("DiscouragedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoFishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("handleOnBackPressed", "Clicked")
            }
        })

        binding.root.post {
            goFishViewModel.joinGame(
                code = intent.getStringExtra("code"),
                uid = intent.getStringExtra("lobbyUid"),
                ip = intent.getStringExtra("lobbyIp"),
                this
            )
        }

        playerViewAdapter.apply {
            setHasStableIds(true)
            itemClickListener = object : ItemClickListener<AppAcc> {
                override fun onItemClicked(item: AppAcc, itemPosition: Int) {
                    CardPickerPopup(application).apply {
                        goFishViewModel.findMyDeck()?.let {
                            showPopup(binding.root, item, it)
                        }
                        adapter.itemClickListener = object : ItemClickListener<Rank> {
                            override fun onItemClicked(card: Rank, itemPosition: Int) {
                                dismiss()
                                item.uid.let { goFishViewModel.write(it, card) }
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

            goFishViewModel.goFishLogic.play.observe(this@GoFishActivity) { plays ->
                goFishViewModel.showAnimation(plays, this)
            }

            goFishViewModel.goFishLogic.gamePlayers.observe(this@GoFishActivity) {
                profile.visibility = View.VISIBLE
                goFishViewModel.findPlayers()?.let { players ->
                    playerViewAdapter.updateItems(players.second)
                    players.first.first().let { me ->
                        goFishViewModel.findMyDeck()
                            ?.let { deck -> cardViewAdapter.updateItems(deck) }
                        yourImage.setImageBitmap(me.second.image)
                        yourName.text = me.second.username
                    }
                }
                deckSize.text = "${goFishViewModel.goFishLogic.getDeckSize()}"
            }
        }
        goFishViewModel.state.map { GoFishUiMapper.map(it) }.observe(this) { updateContent(it) }
    }

    @SuppressLint("SetTextI18n")
    private fun updateContent(data: GoFishUiModel) {
        binding.apply {
            data.startingIn?.let {
                StartingInDialogFragment(it).show(
                    supportFragmentManager,
                    StartingInDialogFragment.TAG
                )
            }
            data.showLobby?.let {
                LobbyDialogFragment(it, goFishViewModel.createSeed).show(
                        supportFragmentManager,
                        LobbyDialogFragment.TAG
                    )
            }
            playerViewAdapter.isYourTurn = data.isYourTurn
            data.playerUid?.let { it1 -> goFishViewModel.setTimer(binding, it1) }
            data.playerName?.let { goFishViewModel.showPlayerToTakeTurn(binding.playerTurn, it) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        goFishViewModel.disconnect()
    }
}