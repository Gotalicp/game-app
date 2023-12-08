package com.example.game_app.game.goFish

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.game_app.R
import com.example.game_app.common.itemClickListener
import com.example.game_app.game.Card

class CardsRecycleView: RecyclerView.Adapter<CardsRecycleView.CardsViewHolder>() {
    private var items = listOf<Card>()
    var itemClickListener: itemClickListener<Card>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return CardsViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(cards: List<Card>){
        items = cards
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
    inner class CardsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cardView = view.findViewById<ImageView>(R.id.card)

        @SuppressLint("SetTextI18n", "DiscouragedApi")
        fun bind(card: Card) {
            val drawableResId = itemView.context.resources.getIdentifier(
                "${card.suit.name.lowercase()}_${card.rank.name.lowercase()}",
                "drawable",
                itemView.context.packageName
            )
            
            cardView.setImageDrawable(ContextCompat.getDrawable(itemView.context, drawableResId))
            itemClickListener?.onItemClicked(card, absoluteAdapterPosition)
        }
    }
}