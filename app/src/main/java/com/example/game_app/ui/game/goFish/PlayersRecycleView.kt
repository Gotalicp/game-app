package com.example.game_app.ui.game.goFish

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.game_app.R
import com.example.game_app.data.itemClickListener
import com.example.game_app.domain.bitmap.BitmapReverser

class PlayersRecycleView : RecyclerView.Adapter<PlayersRecycleView.PlayersViewHolder>() {
    private var bitmapAdapter = BitmapReverser()
    private var items = listOf<GoFishLogic.Player>()
    var itemClickListener: itemClickListener<GoFishLogic.Player>? = null
    var isYourTurn: Boolean = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayersViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_go_fish_player_card, parent, false)
        return PlayersViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(players: List<GoFishLogic.Player>) {
        items = players
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: PlayersViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class PlayersViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val profile = view.findViewById<ImageView>(R.id.profile)
        private val name = view.findViewById<TextView>(R.id.name)
        private val cards = view.findViewById<TextView>(R.id.cards)

        @SuppressLint("SetTextI18n")
        fun bind(player: GoFishLogic.Player) {
            profile.setImageBitmap(player.info.image.let { bitmapAdapter.adapt(it) })
            name.text = player.info.username
            cards.text = player.deck.size.toString()
            view.setOnClickListener {
                if (isYourTurn) {
                    itemClickListener?.onItemClicked(player, absoluteAdapterPosition)
                }
            }
        }
    }
}