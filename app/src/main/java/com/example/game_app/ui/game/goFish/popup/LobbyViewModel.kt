package com.example.game_app.ui.game.goFish.popup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.game_app.data.FireBaseUtilityLobby

class LobbyViewModel(application: Application) : AndroidViewModel(application) {
    private val fireBaseUtility = FireBaseUtilityLobby()
    fun changeTime(
        boolean: Boolean,
        time: String? = null,
        rounds: Int? = null,
        playerLimit: Int? = null
    ) {
        if (boolean) {
            fireBaseUtility.updateLobby(
                secPerTurn = time,
                rounds = rounds,
                playerLimit = playerLimit
            )
        }
    }
}