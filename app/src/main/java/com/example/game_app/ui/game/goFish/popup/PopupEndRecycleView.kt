package com.example.game_app.ui.game.goFish.popup

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import com.example.game_app.R
import com.example.game_app.data.PlayerCache
import com.example.game_app.data.common.RecycleViewAdapter
import com.example.game_app.ui.game.goFish.GoFishLogic

class PopupEndRecycleView: RecycleViewAdapter<GoFishLogic.Player>(
    { oldItem, newItem -> oldItem == newItem },
    { oldItem, newItem -> oldItem == newItem },
    R.layout.item_leaderboard
) {
    override fun createViewHolder(view: View) = PopupLobbyViewHolder(view)
    inner class PopupLobbyViewHolder(view: View) : BaseViewHolder(view) {
        private val score = view.findViewById<TextView>(R.id.score)
        private val name = view.findViewById<TextView>(R.id.name)

        @SuppressLint("SetTextI18n")
        override fun bind(item: GoFishLogic.Player) {
            score.text = item.score.toString()
            name.text = item.uid
        }
    }
}