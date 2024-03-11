package com.example.game_app.data

import com.example.game_app.domain.AccountProvider
import com.example.game_app.domain.firebase.HistoryAdapter
import com.google.firebase.Firebase
import com.google.firebase.database.database
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FireBaseUtilityHistory {
    private var database = Firebase.database
    private val path = "history/${AccountProvider.getUid()}"

    companion object {
        var history = mutableListOf<GameHistory>()
    }

    fun updateHistory(players: Map<String, Int>, game: String, uid: String) {
        database.getReference(path).setValue(
            history.apply {
                if (size == 5) {
                    removeLast()
                }
                add(
                    GameHistory(
                        id = generateId(),
                        players = players,
                        status = GetPlacement.findPlacement(players, uid),
                        date = (LocalDate.now())
                            .format(
                                DateTimeFormatter
                                    .ofPattern("dd/MM/yyyy")
                            ),
                        game = game
                    )
                )
            }
        )
    }

    fun getHistory(callback: (MutableList<GameHistory>?) -> Unit) {
        database.getReference(path).get()
            .addOnSuccessListener { dataSnapshot ->
                HistoryAdapter().adapt(dataSnapshot).let {
                    history = it
                    callback(it)
                }
            }
            .addOnCanceledListener { }
            .addOnFailureListener { }
    }

    private fun generateId() = System.currentTimeMillis().toString()
}

