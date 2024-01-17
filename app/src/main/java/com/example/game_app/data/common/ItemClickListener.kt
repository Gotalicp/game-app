package com.example.game_app.data.common
interface ItemClickListener<T> {
    fun onItemClicked(item: T, itemPosition: Int)
}