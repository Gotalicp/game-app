package com.example.game_app.domain.firebase

import com.example.game_app.data.common.Adapter
import com.example.game_app.data.LobbyInfo
import com.example.game_app.data.PlayerInfo
import com.google.firebase.database.DataSnapshot

class SingleLobbyAdapter : Adapter<DataSnapshot, LobbyInfo> {
    override fun adapt(t: DataSnapshot) = LobbyInfo(
        lobbyName = t.child("lobbyName").getValue(String::class.java) ?: "",
        lobbyUid = t.child("lobbyUid").getValue(String::class.java) ?: "",
        ownerIp = t.child("ownerIp").getValue(String::class.java) ?: "",
        mutableListOf<PlayerInfo>().apply {
            for (playerInfo in t.child("players").children) {
                add(
                    PlayerInfo(
                        username = playerInfo.child("username").getValue(String::class.java) ?: "",
                        uid = playerInfo.child("uid").getValue(String::class.java) ?: "",
                        image = playerInfo.child("image").getValue(String::class.java) ?: "",
                        isHost = playerInfo.child("host").getValue(Boolean::class.java) ?: false,
                    )
                )
            }
        },
        maxPlayerCount = t.child("maxPlayerCount").getValue(Int::class.java) ?: -1,
        gamemode = t.child("gamemode").getValue(String::class.java) ?: "",
        gamemodeId = t.child("gamemodeId").getValue(Int::class.java) ?: -1,
        rounds = t.child("rounds").getValue(Int::class.java) ?: 1,
        secPerTurn = t.child("secPerTurn").getValue(String::class.java) ?:"no limit"
    )
}