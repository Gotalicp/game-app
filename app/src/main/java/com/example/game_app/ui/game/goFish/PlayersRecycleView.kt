package com.example.game_app.ui.game.goFish

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.example.game_app.R
import com.example.game_app.ui.common.RecycleViewAdapter
import com.example.game_app.ui.common.ItemClickListener
import com.example.game_app.domain.game.Card
import com.example.game_app.ui.common.AppAcc

class PlayersRecycleView : RecycleViewAdapter<Pair<MutableList<Card>, AppAcc>>(
    { oldItem, newItem -> oldItem.first.size == newItem.first.size },
    { oldItem, newItem -> oldItem.first == newItem.first },
    R.layout.item_go_fish_player_card
) {
    var itemClickListener: ItemClickListener<AppAcc>? = null
    var isYourTurn: Boolean = false
    override fun createViewHolder(view: View) = PlayersViewHolder(view)
    inner class PlayersViewHolder(private val view: View) : BaseViewHolder(view) {
        var id: String = ""
        val timer: ProgressBar = view.findViewById(R.id.timeTurn)
        private val profile = view.findViewById<ImageView>(R.id.profile)
        private val name = view.findViewById<TextView>(R.id.name)
        private val cards = view.findViewById<TextView>(R.id.cards)

        override fun bind(item: Pair<MutableList<Card>, AppAcc>) {
            super.bind(item)
            id = item.second.uid
            profile.setImageBitmap(item.second.image)
            name.text = item.second.username
            cards.text = item.first.size.toString()
            view.setOnClickListener {
                if (isYourTurn) {
                    itemClickListener?.onItemClicked(item.second, absoluteAdapterPosition)
                }
            }
        }
    }
}
