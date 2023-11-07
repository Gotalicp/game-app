package com.example.game_app.server

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.game_app.data.Messages
import com.example.game_app.host.HostViewModel
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.Executors

class ClientClass(ip: InetAddress): Thread() {

    private var hostAddress = ip
    private lateinit var inputStream: ObjectInputStream
    private lateinit var outputStream: ObjectOutputStream
    private lateinit var socket: Socket

    fun write(message: Messages) {
        try {
            Log.i("Server write", "$message sending")
            outputStream.writeObject(message)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    override fun run() {
        try {
            socket = Socket()
            socket.connect(InetSocketAddress(hostAddress, 8888), 500)
            inputStream = ObjectInputStream(socket.getInputStream())
            outputStream = ObjectOutputStream(socket.getOutputStream())
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        val executor = Executors.newSingleThreadExecutor()
        var handler = Handler(Looper.getMainLooper())

        executor.execute(kotlinx.coroutines.Runnable {
            kotlin.run {
                while (true) {
                    try {
                         val message = inputStream.readObject() as Messages
                            handler.post(Runnable {
                                kotlin.run {
                                    Log.i("client class", message.toString())
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