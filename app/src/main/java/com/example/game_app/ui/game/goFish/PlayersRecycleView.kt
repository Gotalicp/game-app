package com.example.game_app.ui.game.goFish

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.game_app.R
import com.example.game_app.data.common.RecycleViewAdapter
import com.example.game_app.data.common.ItemClickListener
import com.example.game_app.domain.bitmap.BitmapReverser

class PlayersRecycleView : RecycleViewAdapter<GoFishLogic.Player>(
    { oldItem, newItem -> oldItem == newItem },
    { oldItem, newItem -> oldItem == newItem },
    R.layout.item_go_fish_player_card
) {
    var itemClickListener: ItemClickListener<GoFishLogic.Player>? = null
    var isYourTurn: Boolean = false
    override fun createViewHolder(view: View) = PlayersViewHolder(view)
    inner class PlayersViewHolder(private val view: View) : BaseViewHolder(view) {
        private val profile = view.findViewById<ImageView>(R.id.profile)
        private val name = view.findViewById<TextView>(R.id.name)
        private val cards = view.findViewById<TextView>(R.id.cards)

        @SuppressLint("SetTextI18n")
        override fun bind(item: GoFishLogic.Player) {
            super.bind(item)
            profile.setImageBitmap(item.info.image.let { BitmapReverser().adapt(it) })
            name.text = item.info.username
            cards.text = item.deck.size.toString()
            view.setOnClickListener {
                if (isYourTurn) {
                    itemClickListener?.onItemClicked(item, absoluteAdapterPosition)
                }
            }
        }
    }
}