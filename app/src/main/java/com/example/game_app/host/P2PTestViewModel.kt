package com.example.game_app.host

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class P2PTestViewModel(application: Application): AndroidViewModel(application)  {

    private val _isWifiP2pEnabled = MutableLiveData<Boolean>()
    val isWifiP2pEnabled: LiveData<Boolean> = _isWifiP2pEnabled

    private val _peers = MutableLiveData<List<WifiP2pDevice>>()
    val peers: LiveData<List<WifiP2pDevice>> = _peers

    private val _connectionInfo = MutableLiveData<WifiP2pInfo>()
    val connectionInfo: LiveData<WifiP2pInfo> = _connectionInfo

    private lateinit var wifiP2pManager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var receiver: BroadcastReceiver


    init {
        wifiP2pManager = application.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = wifiP2pManager.initialize(application, application.mainLooper, null)

        receiver = WiFiDirectBroadcastReceiver()
        val filter = IntentFilter()
        filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        application.registerReceiver(receiver, filter)
    }

    fun discoverPeers() {
        wifiP2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
            }

            override fun onFailure(reasonCode: Int) {
            }
        })
    }

    fun connectToPeer(device: WifiP2pDevice) {
        val device = _peers.value!![0]
        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
            wps.setup = WpsInfo.PBC
        }
        wifiP2pManager.connect(channel, config, object : WifiP2pManager.ActionListener {

            override fun onSuccess() {
            }

            override fun onFailure(reason: Int) {
            }
        })
    }


    fun sendP2pMessage(message: String) {
        Log.d("P2P Message", "Sending message: $message")
    }

    private inner class WiFiDirectBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                    val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                    _isWifiP2pEnabled.value = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
                }
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                    wifiP2pManager.requestPeers(channel) { peers ->
                        _peers.postValue(ArrayList(peers.deviceList))
                    }
                }
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    val networkInfo = intent
                        .getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)

                    if (networkInfo != null && networkInfo.isConnected) {
                        wifiP2pManager.requestConnectionInfo(channel) { info: WifiP2pInfo? ->
                            _connectionInfo.postValue(info!!)
                        }
                    }
                }
                WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        getApplication<Application>().unregisterReceiver(receiver)
    }
}