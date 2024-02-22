package com.example.game_app.domain.firebase

import com.example.game_app.data.GameHistory
import com.example.game_app.data.common.Adapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.GenericTypeIndicator

class HistoryAdapter : Adapter<DataSnapshot?, GameHistory?> {
    override fun adapt(t: DataSnapshot?): GameHistory? {
        return t?.let {
            GameHistory(
            players = t.child("players").getValue(object : GenericTypeIndicator<Map<String, Int>>() {})?: emptyMap(),
                game = t.child("game").getValue(String::class.java)?:"",
                date = t.child("date").getValue(String::class.java)?:""
            )
        }
    }
}

