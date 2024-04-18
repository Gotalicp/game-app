package com.example.game_app.ui.game.goFish

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.game_app.R
import com.example.game_app.domain.game.GoFishLogic
import com.example.game_app.ui.common.AppAcc
import com.example.game_app.ui.common.ItemClickListener
import com.example.game_app.ui.common.RecycleViewAdapter

class PlayersRecycleView : RecycleViewAdapter<Pair<GoFishLogic.Player, AppAcc>>(
    { oldItem, newItem -> oldItem == newItem },
    { oldItem, newItem -> oldItem.first.deck.size != newItem.first.deck.size },
    R.layout.item_go_fish_player_card
) {
    var itemClickListener: ItemClickListener<AppAcc>? = null
    var isYourTurn: Boolean = false

    override fun createViewHolder(view: View) = PlayersViewHolder(view)
    inner class PlayersViewHolder(private val view: View) : BaseViewHolder(view) {
        private val profile = view.findViewById<ImageView>(R.id.profile)
        private val name = view.findViewById<TextView>(R.id.name)
        private val cards = view.findViewById<TextView>(R.id.cards)
        private val score = view.findViewById<TextView>(R.id.score)

        @SuppressLint("SetTextI18n")
        override fun bind(item: Pair<GoFishLogic.Player, AppAcc>) {
            super.bind(item)
            itemView.tag = item.second.uid
            score.text = "${item.first.player.score}:"
            profile.setImageBitmap(item.second.image)
            name.text = item.second.username
            cards.text = item.first.deck.size.toString()
            view.setOnClickListener {
                if (isYourTurn) {
                    itemClickListener?.onItemClicked(item.second, absoluteAdapterPosition)
                }
            }
        }
    }
}
