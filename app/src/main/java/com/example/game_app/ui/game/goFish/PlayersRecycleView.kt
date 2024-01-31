package com.example.game_app.ui.game.goFish

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.game_app.R
import com.example.game_app.data.Account
import com.example.game_app.data.common.RecycleViewAdapter
import com.example.game_app.data.common.ItemClickListener
import com.example.game_app.domain.bitmap.BitmapReverser

class PlayersRecycleView : RecycleViewAdapter<Pair<GoFishLogic.Player, Account>>(
    { oldItem, newItem -> oldItem != newItem },
    { oldItem, newItem -> oldItem == newItem },
    R.layout.item_go_fish_player_card
) {
    var itemClickListener: ItemClickListener<Pair<GoFishLogic.Player, Account>>? = null
    var isYourTurn: Boolean = false
    override fun createViewHolder(view: View) = PlayersViewHolder(view)
    inner class PlayersViewHolder(private val view: View) : BaseViewHolder(view) {
        private val profile = view.findViewById<ImageView>(R.id.profile)
        private val name = view.findViewById<TextView>(R.id.name)
        private val cards = view.findViewById<TextView>(R.id.cards)

        override fun bind(item: Pair<GoFishLogic.Player, Account>) {
            super.bind(item)
            Log.d("pog", "${item.first.deck}")
            profile.setImageBitmap(item.second.image?.let { BitmapReverser().adapt(it) })
            name.text = item.second.username
            cards.text = item.first.deck.size.toString()
            view.setOnClickListener {
                if (isYourTurn) {
                    itemClickListener?.onItemClicked(item, absoluteAdapterPosition)
                }
            }
        }
    }
}
