package com.example.game_app.ui.main.profile

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.game_app.R
import com.example.game_app.ui.common.ItemClickListener

class HistoryRecycleView : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_HISTORY = 1
        const val VIEW_TYPE_EXTENSION = 0
    }

    private val items = ArrayList<History>()
    var itemClickListener: ItemClickListener<HistoryWrapper>? = null

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is HistoryEntry -> {
                VIEW_TYPE_HISTORY
            }

            is PlayerEntry -> {
                VIEW_TYPE_EXTENSION
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HISTORY -> {
                HistoryViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_history, parent, false)
                )
            }

            VIEW_TYPE_EXTENSION -> {
                PlayerViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_history_player, parent, false)
                )
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HistoryViewHolder -> {
                holder.bind((items[position] as HistoryEntry).history)
            }

            is PlayerViewHolder -> {
                holder.bind((items[position] as PlayerEntry).player)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<History>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val gameMode = view.findViewById<TextView>(R.id.game_mode)
        private val outcome = view.findViewById<TextView>(R.id.outcome)
        private val date = view.findViewById<TextView>(R.id.date)
        private val arrow = view.findViewById<ToggleButton>(R.id.expand)
        fun bind(item: HistoryWrapper) {
            gameMode.text = item.gameName
            outcome.apply {
                text = item.outcome
                setTextColor(ContextCompat.getColor(context, item.outcomeColor))
            }
            date.text = item.date
            arrow.apply {
                rotation = item.arrowRotation
                setOnClickListener {
                    itemClickListener?.onItemClicked(item, bindingAdapterPosition)
                    notifyItemChanged(bindingAdapterPosition)
                }
            }
        }
    }


    inner class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image = view.findViewById<ImageView>(R.id.image)
        private val name = view.findViewById<TextView>(R.id.name)
        private val score = view.findViewById<TextView>(R.id.score)

        fun bind(item: PlayerWrapper) {
            image.setImageBitmap(item.image)
            name.text = item.name
            score.text = item.score
        }
    }
}

sealed class History

data class HistoryEntry(var history: HistoryWrapper) : History()

data class PlayerEntry(val player: PlayerWrapper) : History()

data class HistoryWrapper(
    val id: String,
    val gameName: String,
    val date: String,
    var arrowRotation: Float = 180F,
    val outcome: String,
    val outcomeColor: Int
)

data class PlayerWrapper(
    val name: String,
    val image: Bitmap,
    val score: String
)
