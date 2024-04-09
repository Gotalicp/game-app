package com.example.game_app.data

import android.content.Context
import android.content.SharedPreferences
import com.example.game_app.R

class SharedTheme(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "my_preferences",
        Context.MODE_PRIVATE
    )

    fun saveTheme(theme: Int) {
        sharedPreferences.edit().putInt("theme", theme).apply()
    }

    fun getTheme(): Int {
        return sharedPreferences.getInt("theme", R.style.AppTheme_Blue)
    }
}
