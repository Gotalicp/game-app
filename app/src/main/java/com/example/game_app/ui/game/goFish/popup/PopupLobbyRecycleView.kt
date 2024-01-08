package com.example.game_app.ui.game.goFish.popup

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.game_app.R
import com.example.game_app.data.PlayerInfo
import com.example.game_app.data.common.itemClickListener
import com.example.game_app.domain.bitmap.BitmapReverser

class PopupLobbyRecycleView : RecyclerView.Adapter<PopupLobbyRecycleView.PopupLobbyViewHolder>() {

    private var items = listOf<PlayerInfo>()
    var itemClickListener: itemClickListener<PlayerInfo>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopupLobbyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_popup_profile, parent, false)
        return PopupLobbyViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(players: List<PlayerInfo>){
        items = players
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: PopupLobbyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
    inner class PopupLobbyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image = view.findViewById<ImageView>(R.id.imageProfile)
        private val name = view.findViewById<TextView>(R.id.nameText)

        @SuppressLint("SetTextI18n")
        fun bind(player: PlayerInfo) {
            name.text = player.username
            image.setImageBitmap(BitmapReverser().adapt(player.image))
            itemClickListener?.onItemClicked(player, absoluteAdapterPosition)
            }
        }
    }