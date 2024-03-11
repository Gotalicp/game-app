package com.example.game_app.domain.firebase

import com.example.game_app.data.GameHistory
import com.example.game_app.data.common.Adapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.GenericTypeIndicator

class HistoryAdapter : Adapter<DataSnapshot?, MutableList<GameHistory>> {
    override fun adapt(t: DataSnapshot?): MutableList<GameHistory> {
        val gameHistoryList = mutableListOf<GameHistory>()
        t?.let { snapshot ->
            snapshot.children.forEach { dataSnapshot ->
                val gameHistory = GameHistory(
                    id = dataSnapshot.child("id").getValue(String::class.java) ?: "",
                    players = dataSnapshot.child("players")
                        .getValue(object : GenericTypeIndicator<Map<String, Int>>() {})
                        ?: emptyMap(),
                    game = dataSnapshot.child("game").getValue(String::class.java) ?: "",
                    date = dataSnapshot.child("date").getValue(String::class.java) ?: "",
                    status = dataSnapshot.child("status").getValue(String::class.java) ?: "",
                )
                gameHistoryList.add(gameHistory)
            }
        }
        return gameHistoryList
    }
}
