package com.example.game_app.domain.server

import com.example.game_app.data.common.Adapter

class ByteArrayAdapter : Adapter<ByteArray?, String?> {
    override fun adapt(t: ByteArray?) = t?.decodeToString()
}