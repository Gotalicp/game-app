package com.example.game_app.ui.game

import android.view.View

object GameUiMapper {
    fun map(state: GameStates): GameUiModel = when (state) {
        is GameStates.PreGame -> GameUiModel(showLobby = true, host = state.host)
        is GameStates.Loading -> GameUiModel()
        is GameStates.MyTurn -> GameUiModel(
            isYourTurn = state.isYourTurn,
            playerUid = state.playerUid,
            playerName = state.playerName,
        )

        is GameStates.EndGame -> GameUiModel(showPopup = true)
        is GameStates.StartingIn -> GameUiModel(
            startingIn = state.startingIn,
            showPopup = state.showPopup,
            startingInVisibility = View.VISIBLE
        )
    }
}