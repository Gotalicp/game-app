package com.example.game_app.ui.game.goFish.popup

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import com.example.game_app.R
import com.example.game_app.ui.common.RecycleViewAdapter

data class EndScreenWrapper(
    val name: String,
    val score: String,
    val placement: String
)
class EndScreenAdapter: RecycleViewAdapter<EndScreenWrapper>(
    { oldItem, newItem -> oldItem == newItem },
    { oldItem, newItem -> oldItem == newItem },
    R.layout.item_leaderboard
) {
    override fun createViewHolder(view: View) = PopupLobbyViewHolder(view)
    inner class PopupLobbyViewHolder(view: View) : BaseViewHolder(view) {
        private val score = view.findViewById<TextView>(R.id.score)
        private val name = view.findViewById<TextView>(R.id.name)
        private val placement = view.findViewById<TextView>(R.id.placement)


        @SuppressLint("SetTextI18n")
        override fun bind(item: EndScreenWrapper) {
            name.text = item.name
            score.text = item.score
            placement.text = item.placement
        }
    }
}