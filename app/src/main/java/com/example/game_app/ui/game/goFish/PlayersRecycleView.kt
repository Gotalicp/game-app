package com.example.game_app.ui.game.goFish

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.game_app.R
import com.example.game_app.data.fishy.Account
import com.example.game_app.ui.common.RecycleViewAdapter
import com.example.game_app.ui.common.ItemClickListener
import com.example.game_app.domain.bitmap.BitmapReverser
import com.example.game_app.domain.game.Card
import com.example.game_app.domain.game.Deck
import com.example.game_app.domain.game.GoFishLogic

class PlayersRecycleView : RecycleViewAdapter<Pair<MutableList<Card>, Account>>(
    { oldItem, newItem -> oldItem != newItem },
    { oldItem, newItem -> oldItem == newItem },
    R.layout.item_go_fish_player_card
) {
    var itemClickListener: ItemClickListener<Pair<MutableList<Card>, Account>>? = null
    var isYourTurn: Boolean = false
    override fun createViewHolder(view: View) = PlayersViewHolder(view)
    inner class PlayersViewHolder(private val view: View) : BaseViewHolder(view) {
        private val profile = view.findViewById<ImageView>(R.id.profile)
        private val name = view.findViewById<TextView>(R.id.name)
        private val cards = view.findViewById<TextView>(R.id.cards)

        override fun bind(item: Pair<MutableList<Card>, Account>) {
            super.bind(item)
            profile.setImageBitmap(item.second.image?.let { BitmapReverser().adapt(it) })
            name.text = item.second.username
            cards.text = item.first.size.toString()
            view.setOnClickListener {
                if (isYourTurn) {
                    itemClickListener?.onItemClicked(item, absoluteAdapterPosition)
                }
            }
        }
    }
}
