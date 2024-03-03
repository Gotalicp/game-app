package com.example.game_app.ui.game.chess

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.game_app.data.PlayerCache
import com.example.game_app.domain.AccountProvider
import com.example.game_app.domain.LobbyProvider
import com.example.game_app.domain.game.chess.ChessLogic
import com.example.game_app.domain.server.OkClient
import com.example.game_app.domain.server.OkServer
import com.example.game_app.domain.server.ServerInterface
import com.example.game_app.ui.game.GameStates
import kotlin.random.Random

class ChessViewModel(application: Application) : AndroidViewModel(application){

    private val _state = MutableLiveData<GameStates>()
    val state: LiveData<GameStates> = _state

    private var chessLogic = ChessLogic()

//    private var server: ServerInterface<>? = null

    private var uid = AccountProvider.getAcc().value?.uid
    private var lobby = LobbyProvider.getLobby()
    private var cache = PlayerCache.instance

    var createSeed: (() -> Unit)? = null

//    fun joinGame(code: String? = null, uid: String? = null, ip: String? = null, context: Context) {
//        server = if (code != null && uid != null && ip != null) {
//            OkClient(
//                gameLogic = chessLogic,
//                expectedTClazz = ChessGame.Play::class.java,
//                ip = ip,
//                code = code,
//                port = 8888
//            ).apply {
//                join()
//                _state.value = GameStates.PreGame(false)
//            }
//        } else {
//            OkServer(
//                gameLogic = chessLogic,
//                expectedTClazz = ChessGame.Play::class.java,
//                port = 8888
//            ).apply {
//                join()
//                createSeed = {
//                    Random.nextLong().let { seed ->
//                        chessLogic.updateSeed(seed)
//                        server?.send(seed)
//                    }
//                }
//                _state.value = GameStates.PreGame(true)
//            }
//        }
//    }
}