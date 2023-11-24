package com.example.game_app.server

import com.example.game_app.common.GameLogic
import com.example.game_app.data.Wrapper
import java.io.Serializable
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.util.Enumeration

class ServerHandler<T : Serializable>(private val gameLogic: GameLogic<T>): Thread() {
    private val serverThreads: ArrayList<ServerClass<T>> = ArrayList()
    private var isRunning = false
    private lateinit var serverSocket: ServerSocket

    override fun run() {
        super.run()
        isRunning = true
        while(isRunning) {
            serverSocket = ServerSocket(8888)
            val thread = ServerClass(serverSocket.accept(),gameLogic)
            serverThreads.add(thread)
        }
    }
    fun startGame(){
        isRunning = false
        for(servers in serverThreads){
            servers.startGame()
        }
    }
    fun endGame(){
        for(servers in serverThreads){
            servers.close()
        }
    }
    fun send(t:T){
        for(servers in serverThreads){
            servers.write(Wrapper(t, null))
        }
    }
    fun getLocalInetAddress(): InetAddress? {
        try {
            val networkInterfaces: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface: NetworkInterface = networkInterfaces.nextElement()
                val addresses: Enumeration<InetAddress> = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address: InetAddress = addresses.nextElement()
                    if (!address.isLoopbackAddress && address.isSiteLocalAddress) {
                        return address
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}