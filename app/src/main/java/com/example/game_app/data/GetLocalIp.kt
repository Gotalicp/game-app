package com.example.game_app.data

import android.util.Log
import java.net.NetworkInterface

class GetLocalIp {
    fun getLocalInetAddress(): String? {
        try {
            NetworkInterface.getNetworkInterfaces().let { network ->
                while (network.hasMoreElements()) {
                    network.nextElement().inetAddresses.let { addresses ->
                        while (addresses.hasMoreElements()) {
                            addresses.nextElement().let { address ->
                                if (!address.isLoopbackAddress && address.isSiteLocalAddress) {
                                    Log.d("pog", address.toString().replace("/", ""))
                                    return address.toString().replace("/", "")
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}