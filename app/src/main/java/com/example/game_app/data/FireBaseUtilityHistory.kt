package com.example.game_app.data

import com.example.game_app.domain.AccountProvider
import com.example.game_app.domain.firebase.HistoryAdapter
import com.google.firebase.Firebase
import com.google.firebase.database.database
import java.util.Date

class FireBaseUtilityHistory {
    private var database = Firebase.database
    private val acc = AccountProvider.getAcc()
    private val path = "history/${acc.value?.uid}"

    companion object {
        var history: MutableList<GameHistory> = mutableListOf()
    }
    init {
        getHistory { history = it?: mutableListOf() }
    }

    fun updateHistory(players: Map<String, Int>, game: String) {
        database.getReference(path).setValue(
            history.apply {
                if (size == 5) {
                    removeLast()
                }
                add(
                    GameHistory(
                        players = players,
                        status = "",
                        date = Date().time.toString(),
                        game = game
                    )
                )
            }
        )
    }

    private fun getHistory(callback: (MutableList<GameHistory>?) -> Unit) {
        database.getReference(path).get()
            .addOnSuccessListener {
                callback(HistoryAdapter().adapt(it))
            }
            .addOnCanceledListener { }
            .addOnFailureListener { }
    }
}
