package com.example.game_app.domain.firebase

import com.example.game_app.data.common.Adapter
import com.example.game_app.data.LobbyInfo
import com.google.firebase.database.DataSnapshot

class LobbyInfoAdapter : Adapter<DataSnapshot, LobbyInfo> {
    override fun adapt(t: DataSnapshot) = LobbyInfo(
        code = t.child("code").getValue(String::class.java) ?: "",
        clazz = t.child("clazz").getValue(String::class.java) ?: "",
        lobbyUid = t.child("lobbyUid").getValue(String::class.java) ?: "",
        ownerIp = t.child("ownerIp").getValue(String::class.java) ?: "",
        mutableListOf<String>().apply {
            for (playerInfo in t.child("players").children) {
                add(playerInfo.getValue(String::class.java) ?: "")
            }
        },
        maxPlayerCount = t.child("maxPlayerCount").getValue(Int::class.java) ?: -1,
        rounds = t.child("rounds").getValue(Int::class.java) ?: 1,
        secPerTurn = t.child("secPerTurn").getValue(String::class.java) ?: "no limit"
    )
}