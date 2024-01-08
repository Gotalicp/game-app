package com.example.game_app.data.common
interface Adapter<T, K> {
    fun adapt(t: T): K?
}