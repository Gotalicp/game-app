package com.example.game_app.data

import android.net.wifi.p2p.WifiP2pDevice
import com.example.game_app.common.Adapter

class LobbyAdapter : Adapter<Collection<WifiP2pDevice>, ArrayList<LobbyInfo>> {
    override fun adapt(t: Collection<WifiP2pDevice>): ArrayList<LobbyInfo> {
        val lobbyInfoList = ArrayList<LobbyInfo>()

        for (device in t) {
            val lobbyName = device.deviceName
            val ownerAddress = device.deviceAddress
            val playerCount = 0
            val maxPlayerCount = 4
            val gamemode = "SampleGamemode"
            val connection = "SampleConnection"

            val lobbyInfo = LobbyInfo(lobbyName, ownerAddress, playerCount, maxPlayerCount, gamemode, connection)
            lobbyInfoList.add(lobbyInfo)
        }

        return lobbyInfoList
    }
}