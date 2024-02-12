package com.example.game_app.data

import android.util.Log
import com.example.game_app.data.common.Cache
import com.example.game_app.ui.common.AppAcc

class PlayerCache : Cache<AppAcc> {
    val firebase = FireBaseUtility()

    companion object {
        val instance: PlayerCache by lazy { PlayerCache() }
    }

    private val cache = HashMap<String, AppAcc>()

    override val size: Int
        get() = cache.size

    override fun set(key: String, value: AppAcc) {
        this.cache[key] = value
    }

    override suspend fun get(key: String) =
        cache[key]?.also { Log.d("get", key) } ?: firebase.getUserInfo(key)?.also { set(key, it) }

    override fun remove(key: String) = this.cache.remove(key)
    override fun clear() = this.cache.clear()
}