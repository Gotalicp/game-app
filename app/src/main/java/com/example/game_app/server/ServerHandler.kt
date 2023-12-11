package com.example.game_app.server

import android.util.Log
import com.example.game_app.FireBaseUtility
import com.example.game_app.SharedInformation
import com.example.game_app.common.GameLogic
import com.example.game_app.data.LobbyInfo
import com.example.game_app.data.Wrapper
import java.io.Serializable
import java.net.NetworkInterface
import java.net.ServerSocket

class ServerHandler<T : Serializable>(
    private val gameLogic: GameLogic<T>,
    private val lobbyInfo: LobbyInfo
) : Thread() {
    private val serverThreads: ArrayList<ServerClass<T>> = ArrayList()
    private var isRunning = true
    private lateinit var serverSocket: ServerSocket
    private val fireBaseUtility = FireBaseUtility()
    override fun run() {
        super.run()
        lobbyInfo.ownerIp = getLocalInetAddress() ?: ""
        Log.d("Server", lobbyInfo.ownerIp)
        fireBaseUtility.hostLobby(lobbyInfo)
        serverSocket = ServerSocket(8888)
        while (isRunning) {
            Log.d("Server", "accepting")
            val serverThread = ServerClass(serverSocket.accept().apply {
                Log.d("Server", "accepted")
            }, gameLogic)
            serverThread.run()
            Log.d("Server", "runned")
            serverThreads.add(serverThread)
        }
    }

    fun startGame(seed: Long) {
        isRunning = false
        gameLogic.startGame(seed, SharedInformation.getLobby().value?.players ?: mutableListOf())
        for (servers in serverThreads) {
            servers.startGame(seed)
        }
    }

    fun endGame() {
        for (servers in serverThreads) {
            servers.close()
        }
        fireBaseUtility.destroyLobby(lobbyInfo.lobbyUid)
    }

    fun send(t: T) {
        for (servers in serverThreads) {
            servers.write(Wrapper(t, null))
        }
    }

    private fun getLocalInetAddress(): String? {
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address.isSiteLocalAddress) {
                        return address.toString()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}