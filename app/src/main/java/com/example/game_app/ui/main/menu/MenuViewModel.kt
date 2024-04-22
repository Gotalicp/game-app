package com.example.game_app.ui.main.menu

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.game_app.R
import com.example.game_app.data.firebase.FireBaseUtilityLobby
import com.example.game_app.ui.game.chess.ChessActivity
import com.example.game_app.ui.game.coin.CoinActivity
import com.example.game_app.ui.game.goFish.GoFishActivity

class MenuViewModel(private val application: Application) : AndroidViewModel(application) {
    val list = listOf(
        LibraryGame(
            R.drawable.go_fish, GoFishActivity::class.java, R.string.go_fish_description, false
        ),
        LibraryGame(
            R.drawable.chess, ChessActivity::class.java, R.string.chess_description, false
        ),
        LibraryGame(
            R.drawable.coin_flip, CoinActivity::class.java, R.string.coin_flip_description, true
        )
    )

    private val fireBaseUtilityLobby = FireBaseUtilityLobby()
    
    fun join(code: String, clazz: Class<*>, callback: (Intent?) -> Unit) {
        try {
            if (code.length == 6) {
                fireBaseUtilityLobby.useCode(code) {
                    it?.let {
                        if (it.players.size < it.maxPlayerCount) {
                            callback(
                                Intent(application.applicationContext, clazz).apply {
                                    putExtra("lobbyUid", it.lobbyUid)
                                    putExtra("lobbyIp", it.ownerIp)
                                    putExtra("code", code)
                                })
                        }
                    }

                }
            }
        } catch (ex: Exception) {
            Log.d("Menu joining error", "$ex")
        }
    }

    fun <T> host(clazz: Class<T>) = Intent(application.applicationContext, clazz)
}