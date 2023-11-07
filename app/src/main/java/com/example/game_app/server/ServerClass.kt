package com.example.game_app.server

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.game_app.SharedInformation
import com.example.game_app.data.Messages
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.util.Enumeration
import java.util.concurrent.Executors

class ServerClass :Thread(){

    private lateinit var serverSocket: ServerSocket
    private lateinit var inputStream: ObjectInputStream
    private lateinit var outputStream: ObjectOutputStream
    private var socket: Socket? = null

    @Volatile
    private var isRunning = true

    override fun run() {
        try {
            serverSocket = ServerSocket(8888)
            socket = serverSocket.accept()

            socket?.let{
                inputStream = ObjectInputStream(it.getInputStream())
                outputStream = ObjectOutputStream(it.getOutputStream())
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
                        val message = inputStream.readObject() as Messages
                        if(message.toString().isNotEmpty()) {
                            handler.post(Runnable {
                                kotlin.run {
                                    Log.i("Server class", message.toString())
                                    SharedInformation.updateChat(message)
                                }
                            })
                        }
                    }catch (ex:IOException){
                        ex.printStackTrace()
                    }
                }
            }
        })
        println("Thread is stopping")
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

    fun write(message: Messages) {
        try {
            Log.i("Server write", "$message sending")
            outputStream.writeObject(message)
        } catch (ex: IOException) {
            ex.printStackTrace()
            Log.e("Server", "Error writing message: $ex")
        }
    }
    fun close() {
        try {
            isRunning=false
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