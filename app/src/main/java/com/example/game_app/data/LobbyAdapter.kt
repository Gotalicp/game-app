package com.example.game_app.data

import android.net.wifi.p2p.WifiP2pDevice
import com.example.game_app.common.Adapter

class LobbyAdapter : Adapter<Collection<WifiP2pDevice>, ArrayList<LobbyInfo>> {
    override fun adapt(t: Collection<WifiP2pDevice>): ArrayList<LobbyInfo> {
        val lobbyInfoList = ArrayList<LobbyInfo>()
        for (device in t) {
            lobbyInfoList.add(LobbyInfo(device.deviceName,
                device.deviceAddress,
                1,
                4,
                "SampleGamemode",
                "SampleConnection"))
        }
        return lobbyInfoList
    }
}