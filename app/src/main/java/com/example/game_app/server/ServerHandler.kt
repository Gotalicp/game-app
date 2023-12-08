package com.example.game_app.server

import com.example.game_app.FireBaseViewModel
import com.example.game_app.SharedInformation
import com.example.game_app.common.GameLogic
import com.example.game_app.data.LobbyInfo
import com.example.game_app.data.Wrapper
import java.io.Serializable
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.util.Enumeration

class ServerHandler<T : Serializable>(private val gameLogic: GameLogic<T>, private val lobbyInfo: LobbyInfo): Thread() {
    private val serverThreads: ArrayList<ServerClass<T>> = ArrayList()
    private var isRunning = false
    private lateinit var serverSocket: ServerSocket
    private val fireBase = FireBaseViewModel()
    override fun run() {
        super.run()
        lobbyInfo.ownerIp = getLocalInetAddress()!!
        fireBase.hostLobby(lobbyInfo)
        isRunning = true
        while(isRunning) {
            serverSocket = ServerSocket(8888)
            val thread = ServerClass(serverSocket.accept(),gameLogic)
            serverThreads.add(thread)
        }
    }
    fun startGame(seed: Long){
        isRunning = false
        gameLogic.startGame(seed, SharedInformation.getLobby().value!!.players)
        for(servers in serverThreads){
            servers.startGame(seed)
        }
    }
    fun endGame(){
        for(servers in serverThreads){
            servers.close()
        }
        fireBase.destroyLobby(lobbyInfo.lobbyUid)
    }
    fun send(t:T){
        for(servers in serverThreads){
            servers.write(Wrapper(t, null))
        }
    }
    private fun getLocalInetAddress(): String? {
        try {
            val networkInterfaces: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface: NetworkInterface = networkInterfaces.nextElement()
                val addresses: Enumeration<InetAddress> = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address: InetAddress = addresses.nextElement()
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