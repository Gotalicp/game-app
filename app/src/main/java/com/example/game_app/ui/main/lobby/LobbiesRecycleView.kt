package com.example.game_app.ui.main.lobby

import android.annotation.SuppressLint
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.game_app.R
import com.example.game_app.data.common.ItemClickListener
import com.example.game_app.data.LobbyInfo
import com.example.game_app.data.common.RecycleViewAdapter

class LobbiesRecycleView : RecycleViewAdapter<LobbyInfo>(
    { oldItem, newItem -> oldItem == newItem },
    { oldItem, newItem -> oldItem == newItem },
    R.layout.item_menu
) {
    var itemClickListener: ItemClickListener<LobbyInfo>? = null
    override fun createViewHolder(view: View) = LobbiesViewHolder(view)
    inner class LobbiesViewHolder(view: View) : BaseViewHolder(view) {
        private val button = view.findViewById<Button>(R.id.buttonJoin)
        private val name = view.findViewById<TextView>(R.id.textViewLobbyName)
        private val players = view.findViewById<TextView>(R.id.textViewPlayers)
        private val gamemode = view.findViewById<TextView>(R.id.textViewGameMode)

        @SuppressLint("SetTextI18n")
        override fun bind(item: LobbyInfo) {
            name.text = item.lobbyName
            if (item.maxPlayerCount == item.players.size) {
                button.visibility = View.GONE
            }
            players.text = "${item.players.size}/${item.maxPlayerCount}"
            gamemode.text = item.gamemode
            button.setOnClickListener {
                itemClickListener?.onItemClicked(item, absoluteAdapterPosition)
            }
        }
    }
}