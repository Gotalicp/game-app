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

class DeserializeData : Adapter<ByteArray?, String?> {
    override fun adapt(t: ByteArray?) = t?.decodeToString()
}