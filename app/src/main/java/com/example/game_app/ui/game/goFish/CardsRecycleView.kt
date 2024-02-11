package com.example.game_app.ui.game.goFish

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.game_app.R
import com.example.game_app.ui.common.RecycleViewAdapter
import com.example.game_app.ui.common.ItemClickListener
import com.example.game_app.domain.game.Card

class CardsRecycleView : RecycleViewAdapter<Card>(
    { oldItem, newItem -> oldItem == newItem },
    { oldItem, newItem -> oldItem == newItem },
    R.layout.item_card
) {
    var itemClickListener: ItemClickListener<Card>? = null

    override fun createViewHolder(view: View) = CardViewHolder(view)
    inner class CardViewHolder(private val view: View) : BaseViewHolder(view) {
        private val cardView = view.findViewById<ImageView>(R.id.card)

        @SuppressLint("SetTextI18n", "DiscouragedApi")
        override fun bind(item: Card) {
            try {
                val drawableResId = itemView.context.resources.getIdentifier(
                    "${item.suit.name.lowercase()}_${item.rank.name.lowercase()}",
                    "drawable",
                    itemView.context.packageName
                )

                cardView.setImageDrawable(
                    ContextCompat.getDrawable(
                        itemView.context,
                        drawableResId
                    )
                )
            } catch (e: Exception) {
                Log.e("error", "Card:${item} with error:${e}")
            }
            itemClickListener?.onItemClicked(item, absoluteAdapterPosition)
        }
    }
}