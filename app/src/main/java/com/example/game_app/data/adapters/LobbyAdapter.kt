package com.example.game_app.data.adapters

import com.example.game_app.common.Adapter
import com.example.game_app.data.LobbyInfo
import com.example.game_app.data.PlayerInfo
import com.google.firebase.database.DataSnapshot
import java.util.ArrayList

class LobbyAdapter : Adapter<DataSnapshot, ArrayList<LobbyInfo>> {
    override fun adapt(t: DataSnapshot): ArrayList<LobbyInfo> {
        val lobbyInfoList = ArrayList<LobbyInfo>()
        for (info in t.children) {
            val lobbyName = info.child("lobbyName").getValue(String::class.java)
            val ownerIp = info.child("ownerIp").getValue(String::class.java)
            val lobbyUid = info.child("lobbyUid").getValue(String::class.java)
            val players = mutableListOf<PlayerInfo>()
            val maxPlayerCount = info.child("maxPlayerCount").getValue(Int::class.java)
            val gamemode = info.child("gamemode").getValue(String::class.java)
            val gamemodeId = info.child("gamemodeId").getValue(Int::class.java)
            val connection = info.child("connection").getValue(String::class.java)

            for (playerInfo in info.child("players").children) {
                val player = PlayerInfo(
                    username = playerInfo.child("username").getValue(String::class.java)!!,
                    uid = playerInfo.child("uid").getValue(String::class.java)!!,
                    image = playerInfo.child("image").getValue(String::class.java),
                    isHost = playerInfo.child("host").getValue(Boolean::class.java)!!,
                )
                players.add(player)
            }
            val lobby = LobbyInfo(
                lobbyName = lobbyName!!,
                lobbyUid = lobbyUid!!,
                ownerIp = ownerIp!!,
                players = players,
                maxPlayerCount = maxPlayerCount!!,
                gamemode = gamemode!!,
                gamemodeId = gamemodeId!!,
                connection = connection!!
            )
            lobbyInfoList.add(lobby)
        }
        return lobbyInfoList
    }
}