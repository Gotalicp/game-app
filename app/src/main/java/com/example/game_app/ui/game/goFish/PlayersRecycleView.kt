package com.example.game_app.ui.game.goFish

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.game_app.R
import com.example.game_app.data.common.BaseAdapter
import com.example.game_app.data.common.itemClickListener
import com.example.game_app.domain.bitmap.BitmapReverser

//    fun updateItems(players: List<GoFishLogic.Player>) {
//        items = players
//        notifyDataSetChanged()
class PlayersRecycleView : BaseAdapter<GoFishLogic.Player>(
    areItemsTheSame = { oldItem, newItem -> oldItem == newItem },
    areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
    layoutResId = R.layout.item_go_fish_player_card
) {
    var itemClickListener: itemClickListener<GoFishLogic.Player>? = null
    var isYourTurn: Boolean = false
    override fun createViewHolder(view: View) : PlayersViewHolder = PlayersViewHolder(view)
    inner class PlayersViewHolder(view: View) : BaseViewHolder(view) {
        private val profile = view.findViewById<ImageView>(R.id.profile)
        private val name = view.findViewById<TextView>(R.id.name)
        private val cards = view.findViewById<TextView>(R.id.cards)

        @SuppressLint("SetTextI18n")
        override fun bind(item: GoFishLogic.Player) {
            super.bind(item)
            profile.setImageBitmap(item.info.image.let { BitmapReverser().adapt(it) })
            name.text = item.info.username
            cards.text = item.deck.size.toString()
            itemView.setOnClickListener {
                if (isYourTurn) {
                    itemClickListener?.onItemClicked(item, absoluteAdapterPosition)
                }
            }
        }
    }
}