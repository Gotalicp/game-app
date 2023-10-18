package com.example.game_app.common
interface Adapter<T, K> {
    fun adapt(t: T): K?
}