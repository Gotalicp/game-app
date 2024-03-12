package com.example.game_app.ui.main.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.game_app.R
import com.example.game_app.data.FireBaseUtilityAcc
import com.example.game_app.data.FireBaseUtilityHistory
import com.example.game_app.data.GameHistory
import com.example.game_app.data.PlayerCache
import com.example.game_app.domain.AccountProvider

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    val acc = AccountProvider.getAcc()
    fun getEmail() = FireBaseUtilityAcc().getEmail()

    private val history = FireBaseUtilityHistory.history
    private val _historyInfo = MutableLiveData<List<History>>()
    val historyInfo: LiveData<List<History>>
        get() = _historyInfo

    init {

    }

    suspend fun GameHistory.toPlayerWrapper(): List<PlayerWrapper> {
        return this.players.mapNotNull { map ->
            PlayerCache.instance.get(map.key)?.let {
                it.image?.let { image ->
                    PlayerWrapper(it.username, image, map.value.toString())
                }
            }
        }
    }

    fun GameHistory.toHistoryWrapper(): HistoryWrapper {
        return HistoryWrapper(
            gameName = this.game,
            date = this.date.toString(),
            outcome = this.status,
            outcomeColor = getOutcomeColor(this.game, this.status, this.players)
        )
    }

    private fun getOutcomeColor(game: String, outcome: String, players: Map<String, Int>): Int {
        return when (game) {
            "Chess" -> {
                when (outcome) {
                    "Victory" -> R.color.green
                    "Defeat" -> R.color.red
                    else -> R.color.black
                }
            }

            "GoFish" -> {
                acc.value?.uid?.let {
                    if (players[it]
                            ?.let { it1 -> isValueInTop50Percent(players, it1) } == true
                    ) {
                        R.color.red
                    } else {
                        R.color.green
                    }
                } ?: R.color.black
            }

            else -> {
                R.color.black
            }
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