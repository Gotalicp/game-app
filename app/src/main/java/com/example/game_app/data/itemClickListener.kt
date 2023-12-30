package com.example.game_app.data
interface itemClickListener<T> {
    fun onItemClicked(item: T, itemPosition: Int)
}