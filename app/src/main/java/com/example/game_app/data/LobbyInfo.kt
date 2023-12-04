package com.example.game_app.data

import kotlinx.serialization.Serializable

@Serializable
data class LobbyInfo (
    val lobbyName: String = "",
    var lobbyUid: String = "",
    var ownerIp: String = "",
    var players: MutableList<PlayerInfo> = mutableListOf(),
    val maxPlayerCount:Int = 0,
    val gamemode:String = "",
    val gamemodeId: Int = 0,
    val connection:String = "",
):java.io.Serializable