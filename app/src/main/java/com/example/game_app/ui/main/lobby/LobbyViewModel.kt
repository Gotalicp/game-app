package com.example.game_app.ui.main.lobby

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.game_app.domain.FireBaseUtility
import com.example.game_app.data.LobbyInfo
import com.example.game_app.domain.server.ClientSearchLocalClass
import kotlinx.coroutines.launch
import java.net.InetAddress
import java.net.NetworkInterface

class LobbyViewModel(application: Application) : AndroidViewModel(application) {
    private val _lobbiesList = MutableLiveData<List<LobbyInfo>>()
    val lobbiesList: LiveData<List<LobbyInfo>> get() = _lobbiesList
    private val fireBaseUtility = FireBaseUtility()

    fun checkLocalServers() {
        val foundLobbies = mutableListOf<LobbyInfo>()

        try {
            val baseIpAddress = getLocalIpAddress()
            if (baseIpAddress != null) {
                val startRange = 113
                val endRange = 114

                for (i in startRange..endRange) {
                    val ip = baseIpAddress + i.toString()
                    Log.d("Local IP", "Checking IP: $ip")
                    viewModelScope.launch {
                        ClientSearchLocalClass(ip).apply {
                            start()
                            uid.observeForever {
                                Log.d("uid is ","$it")
                                fireBaseUtility.getLobby(it) { lobbyInfo ->
                                    if (lobbyInfo != null) {
                                        foundLobbies.add(lobbyInfo)
                                        _lobbiesList.postValue(foundLobbies)
                                        uid.removeObserver{}
                                        Log.d("Lobby found on $ip","exz")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun getLocalIpAddress(): String? {
        try {
            NetworkInterface.getNetworkInterfaces().toList()
                .forEach { networkInterface ->
                    networkInterface.inetAddresses.toList().forEach { inetAddress ->
                        if (!inetAddress.isLoopbackAddress && inetAddress is InetAddress) {
                            val ip = inetAddress.hostAddress
                            if (ip.contains(":")
                                    .not()
                            ) { // Check if it's an IPv4 address
                                // Assuming the last part of the IP address represents the device on the local network
                                val parts = ip.split(".")
                                if (parts.size == 4) {
                                    val baseIpAddress =
                                        "${parts[0]}.${parts[1]}.${parts[2]}."
                                    Log.d(
                                        "Local IP",
                                        "Detected base IP address: $baseIpAddress"
                                    )
                                    return baseIpAddress
                                }
                            }
                        }
                    }
                }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return null
    }
}