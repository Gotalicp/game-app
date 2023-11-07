package com.example.game_app.data

import java.net.InetAddress

data class LobbyInfo (
    val lobbyName: String,
    val lobbyUid: String,
    val ownerIp: InetAddress,
    var players: MutableList<PlayerInfo>,
    val maxPlayerCount:Int,
    val gamemode:String,
    val gamemodeId: Int,
    val connection:String,
)