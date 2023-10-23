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
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket


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

    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null
    private var clientSocket: Socket? = null
    private var serverSocket: ServerSocket? = null



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


    fun connectAndSendMessage(message: String) {
        val info = connectionInfo.value

        if (info != null) {
            clientSocket = Socket()
            try {
                clientSocket?.connect(InetSocketAddress(info.groupOwnerAddress, 8888), 5000)

                outputStream = clientSocket?.getOutputStream()

                outputStream?.write(message.toByteArray())
                outputStream?.flush()

                Log.d("P2P Message", "Message sent: $message")
            } catch (e: IOException) {
                Log.e("P2P Message", "Error sending message: ${e.message}")
            }
        }
    }
    private fun startServer() {
        Thread {
            try {
                serverSocket = ServerSocket(8888)
                Log.d("P2P Message", "Server socket started")

                while (true) {
                    clientSocket = serverSocket?.accept()
                    Log.d("P2P Message", "Client connected")

                    outputStream = clientSocket?.getOutputStream()
                    inputStream = clientSocket?.getInputStream()

                    // Start a thread to handle incoming messages
                    Thread {
                        val buffer = ByteArray(1024)
                        var bytesRead: Int

                        try {
                            while (true) {
                                bytesRead = inputStream?.read(buffer) ?: -1
                                if (bytesRead == -1) {
                                    break
                                }

                                val receivedMessage = String(buffer, 0, bytesRead)
                                Log.d("P2P Message", "Message received: $receivedMessage")
                            }
                        } catch (e: IOException) {
                            Log.e("P2P Message", "Error reading message: ${e.message}")
                        }
                    }.start()
                }
            } catch (e: IOException) {
                Log.e("P2P Message", "Error starting server: ${e.message}")
            }
        }.start()
    }

    override fun onCleared() {
        super.onCleared()
        getApplication<Application>().unregisterReceiver(receiver)
    }
}