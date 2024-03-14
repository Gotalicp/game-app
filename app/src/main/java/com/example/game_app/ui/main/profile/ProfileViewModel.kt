package com.example.game_app.ui.main.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.game_app.R
import com.example.game_app.data.firebase.FireBaseUtilityAcc
import com.example.game_app.data.firebase.FireBaseUtilityHistory
import com.example.game_app.data.GameHistory
import com.example.game_app.data.PlayerCache
import com.example.game_app.domain.AccountProvider
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    val acc = AccountProvider
    fun getEmail() = FireBaseUtilityAcc.getEmail()
    private var history = mutableListOf<GameHistory>()

    private val _historyInfo = MutableLiveData<List<History>>()
    val historyInfo: LiveData<List<History>>
        get() = _historyInfo

    fun getHistory() {
        FireBaseUtilityHistory().getHistory { gameHistories ->
            if (gameHistories != null) {
                history = gameHistories
                _historyInfo.value = gameHistories.map { HistoryEntry(it.toHistoryWrapper()) }
            }
        }
    }

    fun onClick(position: Int, item: HistoryWrapper) {
        viewModelScope.launch {
            _historyInfo.value.orEmpty().toMutableList().let { histories ->
                if (item.arrowRotation == 180F) {
                    (histories[position] as? HistoryEntry)?.history?.arrowRotation = 0F
                    history.find { it.id == item.id }
                        ?.toPlayerWrapper()?.let { playerWrappers ->
                            histories.addAll(position + 1, playerWrappers
                                .map { PlayerEntry(it) }.sortedByDescending { it.player.score })
                        }
                } else {
                    (histories[position] as? HistoryEntry)?.history?.arrowRotation = 180F
                    while (position + 1 < histories.size && histories[position + 1] is PlayerEntry) {
                        histories.removeAt(position + 1)
                    }
                }
                _historyInfo.postValue(histories)
            }
        }
    }

    private suspend fun GameHistory.toPlayerWrapper(): List<PlayerWrapper> {
        return players.mapNotNull { map ->
            PlayerCache.instance.get(map.key)?.let {
                it.image?.let { image ->
                    PlayerWrapper(it.username, image, map.value.toString())
                }
            }
        }
    }

    private fun GameHistory.toHistoryWrapper(): HistoryWrapper {
        return HistoryWrapper(
            id = this.id,
            gameName = this.game,
            date = this.date.toString(),
            outcome = this.status,
            outcomeColor = getOutcomeColor(this.players)
        )
    }

    private fun getOutcomeColor(players: Map<String, Int>) = if (players[acc.getUid()]
            ?.let { isValueInTop50Percent(players, it) } == true
    ) {
        R.color.green
    } else {
        R.color.red
    }

    private fun isValueInTop50Percent(map: Map<String, Int>, valueToCheck: Int): Boolean {
        return map.values.sortedDescending().let {
            valueToCheck >= it[(it.size / 2) - 1]
        }
    }
}