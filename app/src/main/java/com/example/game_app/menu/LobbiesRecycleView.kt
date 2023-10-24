package com.example.game_app.menu

import android.annotation.SuppressLint
import android.net.wifi.p2p.WifiP2pDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.game_app.R
import com.example.game_app.common.itemClickListener
import com.example.game_app.data.LobbyAdapter
import com.example.game_app.data.LobbyInfo
import com.example.game_app.host.P2PTestViewModel

class LobbiesRecycleView() : RecyclerView.Adapter<LobbiesRecycleView.LobbiesViewHolder>() {

    private var items = ArrayList<LobbyInfo>()
    var itemClickListener: itemClickListener<LobbyInfo>? = null
    private var lobbyAdapter = LobbyAdapter()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbiesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return LobbiesViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(peers: Collection<WifiP2pDevice>){
        items = lobbyAdapter.adapt(peers)
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: LobbiesViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
    inner class LobbiesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val button = view.findViewById<Button>(R.id.buttonJoin)
        private val name = view.findViewById<TextView>(R.id.textViewLobbyName)
        private val connection = view.findViewById<TextView>(R.id.textViewConnectionType)
        private val players = view.findViewById<TextView>(R.id.textViewPlayers)
        private val gamemode = view.findViewById<TextView>(R.id.textViewGameMode)



        fun bind(lobby: LobbyInfo) {
            name.text = lobby.lobbyName
            connection.text = lobby.connection
            players.text = "${lobby.playerCount}/${lobby.maxPlayerCount}"
            gamemode.text = lobby.gamemode
            button.setOnClickListener {
                itemClickListener?.onItemClicked(lobby, absoluteAdapterPosition)
            }
        }
    }
}