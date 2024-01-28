package com.example.game_app.data

import com.example.game_app.data.common.Adapter
import com.google.gson.Gson
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset

enum class DataType(val code: Byte) {
    LOBBY_INFO(1),
    LONG(2),
    STRING(3)
}

class DeserializeData : Adapter<ByteArray, Any> {
    override fun adapt(t: ByteArray): Any {
        ByteBuffer.wrap(t).let { buffer ->
            buffer.order(ByteOrder.BIG_ENDIAN)
            val dataType = DataType.values().find { it.code == buffer.get() }
                ?: throw IllegalArgumentException("Unknown data type")

            ByteArray(buffer.int).let {data->
                buffer.get(data)

                return when (dataType) {
                    DataType.LOBBY_INFO -> {
                        Gson().fromJson(
                            String(data, Charset.defaultCharset()),
                            LobbyInfo::class.java
                        )
                    }

                    DataType.LONG -> {
                        ByteBuffer.wrap(data).long
                    }

                    DataType.STRING -> {
                        String(data, Charset.defaultCharset())
                    }
                }
            }
        }
    }
}