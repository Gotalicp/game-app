package com.example.game_app.domain.firebase

import com.example.game_app.data.common.Adapter
import com.example.game_app.data.LobbyInfo
import com.example.game_app.data.PlayerInfo
import com.google.firebase.database.DataSnapshot

class SingleLobbyAdapter : Adapter<DataSnapshot, LobbyInfo> {
    override fun adapt(t: DataSnapshot): LobbyInfo {
        val players = mutableListOf<PlayerInfo>()

        for (playerInfo in t.child("players").children) {
            players.add(
                PlayerInfo(
                    username = playerInfo.child("username").getValue(String::class.java) ?: "",
                    uid = playerInfo.child("uid").getValue(String::class.java) ?: "",
                    image = playerInfo.child("image").getValue(String::class.java) ?: "",
                    isHost = playerInfo.child("host").getValue(Boolean::class.java) ?: false,
                )
            )
        }

        return LobbyInfo(
            t.child("lobbyName").getValue(String::class.java) ?: "",
            t.child("lobbyUid").getValue(String::class.java) ?: "",
            t.child("ownerIp").getValue(String::class.java) ?: "",
            players = players,
            t.child("maxPlayerCount").getValue(Int::class.java) ?: -1,
            t.child("gamemode").getValue(String::class.java) ?: "",
            t.child("gamemodeId").getValue(Int::class.java) ?: -1,
            t.child("connection").getValue(String::class.java) ?: ""
        )
    }
}