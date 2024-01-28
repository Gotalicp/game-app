package com.example.game_app.data

import com.xuhao.didi.core.iocore.interfaces.IPulseSendable
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset

class IPulseSendablePulse(private var str: String) : IPulseSendable {
    override fun parse(): ByteArray {
        str.toByteArray(Charset.defaultCharset()).let {
            ByteBuffer.allocate(4 + it.size).apply {
                order(ByteOrder.BIG_ENDIAN)
                putInt(it.size)
                put(it)
                return array()
            }
        }
    }
}