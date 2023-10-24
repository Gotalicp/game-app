package com.example.game_app.data

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import com.example.game_app.host.P2PTestViewModel

class P2PBroadcastReceiver (
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
    private val viewModel : P2PTestViewModel
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    Log.d("WiFiDirectReceiver", "Wi-Fi Direct is enabled")
                } else {
                    Log.d("WiFiDirectReceiver", "Wi-Fi Direct is not enabled")
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                manager.requestPeers(channel , viewModel.peerListListener)
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                manager.requestConnectionInfo(channel, viewModel.connectionListener)
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                val device =
                    intent.getParcelableExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                Log.d("WiFiDirectReceiver", "This device changed: $device")
            }
        }
    }
}