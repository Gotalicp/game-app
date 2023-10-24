package com.example.game_app.host

import android.app.Application
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.game_app.data.P2PBroadcastReceiver

class P2PTestViewModel(application: Application): AndroidViewModel(application)  {

    private val _peers = MutableLiveData<List<WifiP2pDevice>>()
    val peers: LiveData<List<WifiP2pDevice>> = _peers

    private val _connected = MutableLiveData<Boolean>()
    val connected: LiveData<Boolean> = _connected

    private var wifiP2pManager: WifiP2pManager
    private var channel: WifiP2pManager.Channel
    private var receiver: BroadcastReceiver




    init {
        _connected.postValue(false)
        wifiP2pManager = application.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = wifiP2pManager.initialize(application, application.mainLooper, null)
        receiver = P2PBroadcastReceiver(wifiP2pManager, channel, this)

        val intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }
        application.registerReceiver(receiver, intentFilter)
    }

    fun discoverPeers() {
        wifiP2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(
                    getApplication(),
                    "Found.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            override fun onFailure(reason: Int) {
                Toast.makeText(
                    getApplication(),
                    "Discovery Failed $reason. Retry.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
    fun connect2Peers(address: String){
        val config = WifiP2pConfig()
        config.deviceAddress = address
        channel.also { channel ->
            wifiP2pManager.connect(channel, config, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    _connected.postValue(true)
                }
                override fun onFailure(reason: Int) {
                    Toast.makeText(
                        getApplication(),
                        "Connect failed. Retry.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            )}
    }
    fun disconnectFromPeer() {
        wifiP2pManager.removeGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
            }

            override fun onFailure(reason: Int) {
            }
        })
    }
    fun createGroup(){
        wifiP2pManager.createGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
            }
            override fun onFailure(reason: Int) {
                Toast.makeText(
                    getApplication(),
                    "P2P group creation failed. Retry.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
    val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList
        if (refreshedPeers != peers) {
            _peers.postValue(refreshedPeers.toMutableList())
            Log.d(TAG, "Peers found: ${peers.value.toString()}")
        }
        if (refreshedPeers.isEmpty()) {
            Log.d(TAG, "No devices found")
            return@PeerListListener
        }
    }
    val connectionListener = WifiP2pManager.ConnectionInfoListener { info ->

        val groupOwnerAddress: String? = info.groupOwnerAddress.hostAddress

        // After the group negotiation, we can determine the group owner
        // (server).
        if (info.groupFormed && info.isGroupOwner) {
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a group owner thread and accepting
            // incoming connections.
        } else if (info.groupFormed) {
            // The other device acts as the peer (client). In this case,
            // you'll want to create a peer thread that connects
            // to the group owner.
        }
    }
}