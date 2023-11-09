package com.example.game_app.server

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.game_app.SharedInformation
import com.example.game_app.data.Messages
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.Executors

class ClientClass(ip: String): Thread() { private var hostAddress = ip
    private lateinit var inputStream: ObjectInputStream
    private lateinit var outputStream: ObjectOutputStream
    private lateinit var socket: Socket

    @Volatile
    private var isConnected = false

    fun write(message: String) {
        try {
            if (::outputStream.isInitialized && isConnected) {
                Log.i("ClientClass", "$message sending")
                outputStream.writeObject(message)
                Log.i("ClientClass", "Send")
                outputStream.reset()
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
            @SuppressLint("SuspiciousIndentation")
            override fun run() {
        try {
            socket = Socket()
            val ip = InetSocketAddress(hostAddress, 8888)
            socket.connect(ip, 500)
            outputStream = ObjectOutputStream(socket.getOutputStream())
            outputStream.flush()
            Log.d("connection", "Connection established")
            inputStream = ObjectInputStream(socket.getInputStream())
            Log.d("connection", "Connection established")
            isConnected = true
            Log.d("connected","connected")
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        val executor = Executors.newSingleThreadExecutor()
        var handler = Handler(Looper.getMainLooper())

        executor.execute(kotlinx.coroutines.Runnable {
            kotlin.run {
                while (isConnected) {
                    try {
                         val message = inputStream.readObject() as String
                            handler.post(Runnable {
                                kotlin.run {
                                    Log.i("client class", message)
                                    SharedInformation.updateChat(Messages("me",message,"now"))
                                }
                            })
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                }
            }
        })
    }
}