package com.example.game_app.game.goFish

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.game_app.R
import com.example.game_app.common.itemClickListener
import com.example.game_app.data.PlayerInfo
import com.example.game_app.data.adapters.BitmapReverser

class PlayersRecycleView: RecyclerView.Adapter<PlayersRecycleView.PlayersViewHolder>() {
    private var bitmapAdapter = BitmapReverser()
    private var items = listOf<PlayerInfo>()
    var itemClickListener: itemClickListener<PlayerInfo>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_go_fish_player_card, parent, false)
        return PlayersViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(players: List<PlayerInfo>){
        items = players
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: PlayersViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
    inner class PlayersViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val profile = view.findViewById<ImageView>(R.id.profile)
        private val name = view.findViewById<TextView>(R.id.name)

        @SuppressLint("SetTextI18n")
        fun bind(player: PlayerInfo) {
            profile.setImageBitmap(bitmapAdapter.adapt(player.image!!))
            name.text = player.username
            itemClickListener?.onItemClicked(player, absoluteAdapterPosition)
        }
    }
}