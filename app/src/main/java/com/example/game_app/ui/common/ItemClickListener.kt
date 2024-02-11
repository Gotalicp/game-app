package com.example.game_app.ui.common
interface ItemClickListener<T> {
    fun onItemClicked(item: T, itemPosition: Int)
}