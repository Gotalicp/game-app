package com.example.game_app.data

import kotlinx.serialization.Serializable

@Serializable
data class LobbyInfo (
    val code: String = "",
    var lobbyUid: String = "",
    var ownerIp: String = "",
    var players: MutableList<String> = mutableListOf(),
    val maxPlayerCount:Int = 0,
    val gameMode:String = "",
    val gameModeId: Int = 0,
    val rounds: Int = 1,
    val secPerTurn: String = "no limit"
):java.io.Serializable