package com.example.game_app.domain.server

import android.util.Log
import com.example.game_app.data.DeserializeData
import com.example.game_app.data.GameLogic
import com.example.game_app.data.ISendableData
import com.example.game_app.data.SharedInformation
import com.example.game_app.domain.FireBaseUtility
import com.example.game_app.domain.Rank
import com.example.game_app.ui.game.goFish.Play
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.xuhao.didi.core.iocore.interfaces.ISendable
import com.xuhao.didi.core.pojo.OriginalData
import com.xuhao.didi.socket.client.sdk.OkSocket
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClient
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClientIOCallback
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClientPool
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerActionListener
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerShutdown
import java.io.Serializable
import java.net.NetworkInterface

class OkServerClass<T : Serializable>(
    private val gameLogic: GameLogic<T>,
    private val expectedTClazz: Class<T>

) {
    private val fireBaseUtility = FireBaseUtility()
    private val register = OkSocket.server(8888)

    init {
        OkSocketOptions.setIsDebug(true);
        fireBaseUtility.hostLobby(getLocalInetAddress() ?: "")
    }

    private var serverManager = register.registerReceiver(object : IServerActionListener {
        override fun onServerListening(serverPort: Int) {
            Log.d("OkServer", "Listening")
        }

        override fun onClientConnected(
            client: IClient?,
            serverPort: Int,
            clientPool: IClientPool<*, *>?
        ) {
            send(SharedInformation.getAcc().value?.uid.toString())
            Log.d("OkServer", "Client connected")
            client?.addIOCallback(object : IClientIOCallback {
                override fun onClientRead(
                    originalData: OriginalData?,
                    client: IClient?,
                    clientPool: IClientPool<IClient, String>?
                ) {
                    Log.d("OkServer", "Client Wrote")
                    originalData?.bodyBytes?.let { body ->
                        DeserializeData().adapt(body).toString().let {
                                try {
                                    Gson().fromJson(it, expectedTClazz).let {play->
                                        send(play)
                                        Log.d("DATA", play.toString())
                                        gameLogic.turnHandling(play)
                                    }
                                } catch (_: JsonSyntaxException) {
                                }

                                try {
                                    val data = Gson().fromJson(it, String::class.java)
                                    Log.d("DATA", data.toString())
                                } catch (_: JsonSyntaxException) {
                                }
                            }
                    }
                }

                override fun onClientWrite(
                    sendable: ISendable?,
                    client: IClient?,
                    clientPool: IClientPool<IClient, String>?
                ) {
                    Log.d("OkServer", "Client Read")
                }
            })
        }

        override fun onClientDisconnected(
            client: IClient?,
            serverPort: Int,
            clientPool: IClientPool<*, *>?
        ) {

        }

        override fun onServerWillBeShutdown(
            serverPort: Int,
            shutdown: IServerShutdown?,
            clientPool: IClientPool<*, *>?,
            throwable: Throwable?
        ) {
            fireBaseUtility.destroyLobby()
            shutdown?.shutdown()
        }

        override fun onServerAlreadyShutdown(serverPort: Int) {
            Log.d("OkServer", "onServerAlreadyShutdown")
        }
    }).apply {
        listen()
    }

    fun <J> send(data: J) {
        serverManager.clientPool.sendToAll(
            ISendableData(Gson().toJson(data))
        )
    }

    fun stopServer() {
        serverManager.shutdown()
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
}