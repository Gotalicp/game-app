package com.example.game_app.data
interface Adapter<T, K> {
    fun adapt(t: T): K?
}