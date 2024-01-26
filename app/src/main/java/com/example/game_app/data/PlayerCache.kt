package com.example.game_app.data

import android.util.Log
import com.example.game_app.domain.FireBaseUtility
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class PlayerCache : Cache {
    val firebase = FireBaseUtility()

    companion object {
        val instance: PlayerCache by lazy { PlayerCache() }
    }

    private val cache = HashMap<String, Account>()

    override val size: Int
        get() = cache.size

    override fun set(key: String, value: Account) {
        this.cache[key] = value
    }

    override suspend fun get(key: String) =
        cache[key]?.also { Log.d("get", key) } ?: firebase.getUserInfo(key)?.also { set(key, it) }

    override fun remove(key: String) = this.cache.remove(key)
    override fun clear() = this.cache.clear()
}