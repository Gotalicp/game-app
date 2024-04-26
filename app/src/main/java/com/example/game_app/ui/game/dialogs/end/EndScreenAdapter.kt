package com.example.game_app.ui.game.dialogs.end

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import com.example.game_app.R
import com.example.game_app.ui.common.RecycleViewAdapter

class EndScreenAdapter : RecycleViewAdapter<EndWrapper>(
    { oldItem, newItem -> oldItem == newItem },
    { oldItem, newItem -> oldItem.score == newItem.score },
    R.layout.item_leaderboard
) {
    override fun createViewHolder(view: View) = PopupLobbyViewHolder(view)
    inner class PopupLobbyViewHolder(view: View) : BaseViewHolder(view) {
        private val score = view.findViewById<TextView>(R.id.score)
        private val name = view.findViewById<TextView>(R.id.name)
        private val placement = view.findViewById<TextView>(R.id.placement)


        @SuppressLint("SetTextI18n")
        override fun bind(item: EndWrapper) {
            name.text = item.name
            score.text = item.score
            placement.text = item.placement
        }
    }
}