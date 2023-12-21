package com.example.game_app.game.goFish.popup

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.game_app.R
import com.example.game_app.common.itemClickListener
import com.example.game_app.game.Card
import com.example.game_app.game.Rank

class PopupPickCardRecycleView : RecyclerView.Adapter<PopupPickCardRecycleView.PopupPickCardViewHolder>() {

    private var items = listOf<Rank>()
    var itemClickListener: itemClickListener<Rank>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopupPickCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return PopupPickCardViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(cards: List<Card>){
        items = cards.distinctBy { it.rank }.map{it.rank}
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: PopupPickCardViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
    inner class PopupPickCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cardView = view.findViewById<ImageView>(R.id.card)

        @SuppressLint("SetTextI18n", "DiscouragedApi")
        fun bind(card: Rank) {
            try {
                val drawableResId = itemView.context.resources.getIdentifier(
                    card.name.lowercase(),
                    "drawable",
                    itemView.context.packageName
                )

                cardView.setImageDrawable(ContextCompat.getDrawable(itemView.context, drawableResId))
            }catch (e : Exception){
                Log.e("error", "Card:${card} with error:${e}")
            }
            cardView.setOnClickListener {
                itemClickListener?.onItemClicked(card, absoluteAdapterPosition)
            }
        }
    }
}