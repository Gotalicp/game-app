package com.example.game_app.server

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.game_app.SharedInformation
import com.example.game_app.data.Messages
import com.example.game_app.game.GameLogic
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.util.Enumeration
import java.util.concurrent.Executors

class ServerClass<T : Serializable>(private val gameLogic: GameLogic<T>) : Thread() {

    private lateinit var serverSocket: ServerSocket
    private lateinit var inputStream: ObjectInputStream
    private lateinit var outputStream: ObjectOutputStream
    private var socket: Socket? = null

    @Volatile
    private var isRunning = true
    private var isConnected = false


    override fun run() {
        try {
            serverSocket = ServerSocket(8888)
            Log.i("ServerClass","started")
            socket = serverSocket.accept()
                socket?.let{
                    inputStream = ObjectInputStream(it.getInputStream())
                    Log.i("ServerClass","input")
                    outputStream = ObjectOutputStream(it.getOutputStream())
                    Log.i("ServerClass","output")
                    isConnected = true
                    isRunning = true
                    gameLogic.startGame()
            }
        }catch (ex: IOException){
            ex.printStackTrace()
            Log.i("Server write","$ex")
        }
        val executors = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executors.execute(Runnable{
            kotlin.run {
                while (isRunning){
                    try {
                        val play = inputStream.readObject() as T
                        if(play.isNotEmpty()) {
                            handler.post(Runnable {
                                kotlin.run {
                                    Log.i("Server class", play.toString())
//                                    SharedInformation.updateChat(Messages("me ",play.toString()," now"))
                                    gameLogic.turnHandling(play)
                                    if(gameLogic.gameEnded()){
                                        gameLogic.endGame()
                                    }
                                }
                            })
                        }
                    }catch (ex:IOException){
                        ex.printStackTrace()
                    }
                }
            }
        })
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

    fun write(play: T) {
        try {
            if (::outputStream.isInitialized && isConnected) {
                Log.i("Server write", "${play.toString()} sending")
                Thread {
                    outputStream.writeObject(play)
                }.start()
                Log.i("Server write", "Send")
            } else {
                Log.e(
                    "ClientClass",
                    "Error writing message: outputStream not initialized or connection not established."
                )
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            Log.e("ClientClass", "Error writing message: $ex")
        }
    }
    fun close() {
        try {
            isRunning = false
            isConnected = false
            if(socket != null) {
                outputStream.close()
                inputStream.close()
                socket!!.close()
            }else{
                serverSocket.close()
            }
            Log.i("Server", "Closed streams and socket")
        } catch (ex: IOException) {
            ex.printStackTrace()
            Log.e("Server", "Error closing resources: $ex")
        }
    }
}