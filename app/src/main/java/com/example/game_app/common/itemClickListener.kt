package com.example.game_app.common
interface itemClickListener<T> {
    fun onItemClicked(item: T, itemPosition: Int)
}