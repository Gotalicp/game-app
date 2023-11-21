package com.example.game_app.lobby

import com.example.game_app.data.PlayerInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.game_app.R
import com.example.game_app.common.itemClickListener
import com.example.game_app.data.adapters.BitmapReverser

class LobbyRecycleViewAdapter(isHost:Boolean) : RecyclerView.Adapter<LobbyRecycleViewAdapter.LobbyViewHolder>() {
    private val adapter = BitmapReverser()

    private var items = ArrayList<PlayerInfo>()
    var itemClickListener: itemClickListener<PlayerInfo>? = null
    private val host = isHost

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
     = LobbyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_lobby, parent, false), host)

    override fun onBindViewHolder(holder: LobbyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
    inner class LobbyViewHolder(view: View, isHost: Boolean) : RecyclerView.ViewHolder(view) {
        private var image = view.findViewById<ImageView>(R.id.profileImage)
        private val button = view.findViewById<Button>(R.id.kickButton)
        private var text = view.findViewById<TextView>(R.id.profileName)
        private val host = isHost
        fun bind(player: PlayerInfo) {
            Glide.with(image)
                .load(adapter.adapt(player.image!!))
                .centerCrop()
                .into(image)
            text.text = player.username
            if (host) {
                button.setOnClickListener { itemClickListener?.onItemClicked(player, absoluteAdapterPosition) }
            }else{
                button.visibility = View.GONE
            }
        }
    }
}