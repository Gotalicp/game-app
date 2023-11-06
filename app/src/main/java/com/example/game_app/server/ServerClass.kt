package com.example.game_app.server

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.game_app.data.Messages
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

class ServerClass :Thread(){

    private lateinit var serverSocket: ServerSocket
    private lateinit var inputStream: ObjectInputStream
    private lateinit var outputStream: ObjectOutputStream
    lateinit var socket: Socket
    override fun run() {
        try {
            serverSocket = ServerSocket(8888)
            socket = serverSocket.accept()
            Log.i("Server write","accept")
            inputStream = ObjectInputStream(socket.getInputStream())
            Log.i("Server write","input")
            outputStream = ObjectOutputStream(socket.getOutputStream())
            Log.i("Server write","output")

        }catch (ex: IOException){
            ex.printStackTrace()
            Log.i("Server write","$ex")

        }
        Log.i("Server write","after")

        val executors = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executors.execute(Runnable{
            kotlin.run {
                while (true){
                    try {
                        val message =  inputStream.readObject() as Messages
                        if(!message.toString().isNullOrEmpty()) {
                            handler.post(Runnable {
                                kotlin.run {
                                    Log.i("Server class", message.toString())
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
    private fun getLocalIpAddress(): String? {
        try {
            val interfaces = InetAddress.getAllByName(InetAddress.getLocalHost().hostName)
            for (inetAddress in interfaces) {
                if (!inetAddress.isLoopbackAddress) {
                    return inetAddress.hostAddress
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Server", "Error getting local IP address: $e")
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
            outputStream.close()
            inputStream.close()
            socket.close()
            Log.i("Server", "Closed streams and socket")
        } catch (ex: IOException) {
            ex.printStackTrace()
            Log.e("Server", "Error closing resources: $ex")
        }
    }
}