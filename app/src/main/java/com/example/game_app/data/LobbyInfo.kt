package com.example.game_app.data

data class LobbyInfo (
    val lobbyName: String,
    val ownerIp: String,
    val players: MutableList<PlayerInfo>,
    val maxPlayerCount:Int,
    val gamemode:String,
    val gamemodeId: Int,
    val connection:String,
)