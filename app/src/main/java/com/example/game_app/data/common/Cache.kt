package com.example.game_app.data.common

import com.example.game_app.data.fishy.Account

interface Cache {
    val size: Int
    fun set(key: String, value: Account)
    suspend fun get(key: String): Account?
    fun remove(key: String): Account?
    fun clear()
}