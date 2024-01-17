package com.example.game_app.domain.firebase

import com.example.game_app.data.common.Adapter
import com.example.game_app.data.LobbyInfo
import com.example.game_app.data.PlayerInfo
import com.google.firebase.database.DataSnapshot
import java.util.ArrayList

class LobbyAdapter : Adapter<DataSnapshot, ArrayList<LobbyInfo>> {
    override fun adapt(t: DataSnapshot): ArrayList<LobbyInfo> {
        val lobbyInfoList = ArrayList<LobbyInfo>()
        for (info in t.children) {
            val players = mutableListOf<PlayerInfo>()
            for (playerInfo in info.child("players").children) {
                val player = PlayerInfo(
                    username = playerInfo.child("username").getValue(String::class.java)?:"",
                    uid = playerInfo.child("uid").getValue(String::class.java)?:"",
                    image = playerInfo.child("image").getValue(String::class.java)?:"",
                    isHost = playerInfo.child("host").getValue(Boolean::class.java)?:false,
                )
                players.add(player)
            }
            LobbyInfo(
                lobbyName = info.child("lobbyName").getValue(String::class.java)?:"",
                lobbyUid = info.child("lobbyUid").getValue(String::class.java)?:"",
                ownerIp = info.child("ownerIp").getValue(String::class.java)?:"",
                players = players,
                maxPlayerCount = info.child("maxPlayerCount").getValue(Int::class.java)?:-1,
                gamemode = info.child("gamemode").getValue(String::class.java)?:"",
                gamemodeId = info.child("gamemodeId").getValue(Int::class.java)?:-1,
                rounds = info.child("rounds").getValue(Int::class.java)?:1,
                secPerTurn = info.child("secPerTurn").getValue(String::class.java)?:"no limit"
            ).let { lobbyInfoList.add(it) }
        }
        return lobbyInfoList
    }
}