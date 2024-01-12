package com.example.game_app.domain.server

import android.util.Log
import com.example.game_app.domain.FireBaseUtility
import com.example.game_app.data.SharedInformation
import com.example.game_app.data.GameLogic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.Serializable
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket

class ServerHandler<T : Serializable>(
    private val gameLogic: GameLogic<T>,
) : Thread() {

    private val serverThreads: ArrayList<ServerClass<T>> = ArrayList()
    private var canJoinServer = true
    private var serverSocket = ServerSocket(8888)
    private val fireBaseUtility = FireBaseUtility()
    private val lobby = SharedInformation.getLobby()
    private val acc = SharedInformation.getAcc()

    override fun run() {
        super.run()
        fireBaseUtility.hostLobby(getLocalInetAddress() ?: "")
        while (canJoinServer) {
            Log.d("Server", "accepting")
            ServerClass(serverSocket.accept(), gameLogic).apply {
                run()
                write(acc.value?.uid)
                serverThreads.add(this)
                Log.d("Server", "someone joined")
            }
        }
    }

    fun startGame(seed: Long) {
        canJoinServer = false
        cleanupServers()
        lobby.value?.players?.let { gameLogic.startGame(seed, it) }
        send(seed)
    }

    fun endGame() {
        for (servers in serverThreads) {
            servers.close()
        }
        fireBaseUtility.destroyLobby()
    }

    fun <T> send(t: T) {
        for (servers in serverThreads) {
            servers.write(t)
        }
    }

    private fun getLocalInetAddress(): String? {
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

    private fun cleanupServers() {
        serverThreads.iterator().let {
            while (it.hasNext()) {
                if (!it.next().isRunning) {
                    it.remove()
                }
            }
        }
    }
}