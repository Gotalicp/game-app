package com.example.game_app.ui.main.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.game_app.R
import com.example.game_app.data.FireBaseUtilityAcc
import com.example.game_app.data.FireBaseUtilityHistory
import com.example.game_app.data.GameHistory
import com.example.game_app.data.PlayerCache
import com.example.game_app.domain.AccountProvider
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    val acc = AccountProvider.getAcc()
    private val uid = AccountProvider.getUid()
    fun getEmail() = FireBaseUtilityAcc().getEmail()
    private var history = mutableListOf<GameHistory>()

    private val _historyInfo = MutableLiveData<List<History>>()
    val historyInfo: LiveData<List<History>>
        get() = _historyInfo

    init {
        FireBaseUtilityHistory().getHistory { gameHistories ->
            if (gameHistories != null) {
                history = gameHistories
                _historyInfo.value = gameHistories.map { HistoryEntry(it.toHistoryWrapper()) }
            }
        }
    }

    fun onClick(position: Int, item: HistoryWrapper) {
        viewModelScope.launch {
            val captured = _historyInfo.value.orEmpty().toMutableList()
            if (item.arrowRotation == 180F) {
                (captured[position] as? HistoryEntry)?.history?.arrowRotation = 0F
                history.find { it.id == item.id }
                    ?.toPlayerWrapper()?.let { playerWrappers ->
                        captured.addAll(position + 1, playerWrappers
                            .map { PlayerEntry(it) })
                    }
            } else {
                (captured[position] as? HistoryEntry)?.history?.arrowRotation = 180F
                while (position + 1 < captured.size && captured[position + 1] is PlayerEntry) {
                    captured.removeAt(position + 1)
                }
            }
            _historyInfo.postValue(captured)
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

    private fun getOutcomeColor(players: Map<String, Int>): Int {
        return if (players[uid]
                ?.let { it1 -> isValueInTop50Percent(players, it1) } == true
        ) {
            R.color.green
        } else {
            R.color.red
        }
    }

    private fun isValueInTop50Percent(map: Map<String, Int>, valueToCheck: Int): Boolean {
        map.values.apply {
            toList().let {
                it.sortedDescending()
                return valueToCheck >= it[size / 2]
            }
        }
    }
}