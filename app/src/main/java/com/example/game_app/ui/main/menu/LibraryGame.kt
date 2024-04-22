package com.example.game_app.ui.main.menu

data class LibraryGame(
    val imageId: Int,
    val clazz: Class<*>,
    val description: Int,
    val isSinglePlayer: Boolean
)