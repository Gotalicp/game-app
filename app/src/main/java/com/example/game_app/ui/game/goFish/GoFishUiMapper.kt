package com.example.game_app.ui.game.goFish

object GoFishUiMapper {
    fun map(state: GoFishViewModel.State): GoFishUiModel = when (state) {
        is GoFishViewModel.State.PreGame -> GoFishUiModel(showLobby = true)
        is GoFishViewModel.State.Loading -> GoFishUiModel()
        is GoFishViewModel.State.MyTurn -> GoFishUiModel(isYourTurn = true)
        is GoFishViewModel.State.EndGame -> GoFishUiModel(showScores = true)
        else -> {
            GoFishUiModel()
        }
    }
}