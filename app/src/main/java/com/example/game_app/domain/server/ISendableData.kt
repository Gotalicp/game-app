package com.example.game_app.domain.server

import com.xuhao.didi.core.iocore.interfaces.ISendable
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset

class ISendableData(private var str: String) : ISendable {
    override fun parse(): ByteArray {
        str.toByteArray(Charset.defaultCharset()).let { payload ->
            ByteBuffer.allocate(4 + payload.size).apply {
                order(ByteOrder.BIG_ENDIAN)
                putInt(payload.size)
                put(payload)
                return array()
            }
        }
    }
}