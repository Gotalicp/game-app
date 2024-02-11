package com.example.game_app.data

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
                                    return address.toString()
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