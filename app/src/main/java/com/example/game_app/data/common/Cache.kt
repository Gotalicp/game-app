package com.example.game_app.data.common

interface Cache<T> {
    val size: Int
    fun set(key: String, value: T)
    suspend fun get(key: String): T?
    fun remove(key: String): T?
    fun clear()
}