package com.example.game_app.domain.server

import android.util.Log
import com.example.game_app.domain.game.GameLogic
import com.example.game_app.domain.SharedInformation
import com.example.game_app.data.FireBaseUtility
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
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

class OkServer<T : Serializable>(
    override val gameLogic: GameLogic<T>,
    override val expectedTClazz: Class<T>, override val port: Int
) : ServerInterface<T> {
    private val fireBaseUtility = FireBaseUtility()
    private val register = OkSocket.server(port)

    init {
        OkSocketOptions.setIsDebug(true);
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
                        ByteArrayAdapter().adapt(body).toString().let {
                            try {
                                Gson().fromJson(it, expectedTClazz).let { play ->
                                    send(play)
                                    Log.d("DATA", play.toString())
                                }
                            } catch (_: JsonSyntaxException) {}
                            try {
                                val data = Gson().fromJson(it, String::class.java)
                                Log.d("DATA", data.toString())
                            } catch (_: JsonSyntaxException) {}
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
    })

    override fun <J> send(data: J) {
        serverManager.clientPool.sendToAll(
            ISendableData(Gson().toJson(data))
        )
        if(expectedTClazz.isInstance(data)){
            gameLogic.turnHandling(data as T)
        }
    }

    override fun join() {
        serverManager.listen()
    }

    override fun disconnect() {
        serverManager.shutdown()
    }

}