package com.example.game_app.data

data class LobbyInfo (
    val lobbyName: String,
    val ownerAddress: String,
    val playerCount:Int,
    val maxPlayerCount:Int,
    val gamemode:String,
    val connection:String,
)