package com.example.game_app.ui.game.goFish

import android.view.View

object GoFishUiMapper {
    fun map(state: GoFishViewModel.State): GoFishUiModel = when (state) {
        is GoFishViewModel.State.PreGame -> GoFishUiModel(showPopup = true)
        is GoFishViewModel.State.Loading -> GoFishUiModel()
        is GoFishViewModel.State.MyTurn -> GoFishUiModel(
            isYourTurn = state.isYourTurn,
            playerUid = state.playerUid,
            playerName = state.playerName,
        )
        is GoFishViewModel.State.EndGame -> GoFishUiModel(showPopup = true)
        is GoFishViewModel.State.StartingIn -> GoFishUiModel(startingIn = state.startingIn, showPopup = state.showPopup, startingInVisibility = View.VISIBLE)
    }
}