package com.example.game_app.data

import com.xuhao.didi.core.iocore.interfaces.ISendable
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset

class ISendableData(private val type: DataType, private var str: String) : ISendable {
    override fun parse(): ByteArray {
        str.toByteArray(Charset.defaultCharset()).let {
            ByteBuffer.allocate(4 + it.size).apply {
                order(ByteOrder.BIG_ENDIAN)
                putInt(it.size)
                put(type.code)
                put(it)
                return array()
            }
        }
    }
}