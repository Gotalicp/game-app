package com.example.game_app.data.common
interface itemClickListener<T> {
    fun onItemClicked(item: T, itemPosition: Int)
}