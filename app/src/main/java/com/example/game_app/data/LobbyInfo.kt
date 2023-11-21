package com.example.game_app.data

import kotlinx.serialization.Serializable

@Serializable
data class LobbyInfo (
    val lobbyName: String,
    val lobbyUid: String,
    val ownerIp: String,
    var players: MutableList<PlayerInfo>,
    val maxPlayerCount:Int,
    val gamemode:String,
    val gamemodeId: Int,
    val connection:String,
):java.io.Serializable