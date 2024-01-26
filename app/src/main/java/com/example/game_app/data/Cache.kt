package com.example.game_app.data

interface Cache {
    val size: Int
    fun set(key: String, value: Account)
    suspend fun get(key: String): Account?
    fun remove(key: String): Account?
    fun clear()
}