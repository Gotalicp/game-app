package com.example.game_app.ui.main.profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.game_app.R

class HistoryRecycleView : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_HISTORY = 1
        const val VIEW_TYPE_EXTENSION = 0
    }

    private val items = ArrayList<WorkoutEntry>()

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is HistoryEntry -> {
                VIEW_TYPE_HISTORY
            }

            is ExtendedEntry -> {
                VIEW_TYPE_EXTENSION
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HISTORY -> {
                ExerciseViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_history, parent, false)
                )
            }

            VIEW_TYPE_EXTENSION -> {
                SetViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_history_extended, parent, false)
                )
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ExerciseViewHolder -> {
                holder.bind((items[position] as HistoryEntry).exerciseEntry)
            }

            is SetViewHolder -> {
                holder.bind((items[position] as ExtendedEntry).setEntry)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<WorkoutEntry>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        fun bind(item: HistoryWrapper) {
        }
    }


    inner class SetViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: ExtendedHistoryWrapper) {

        }
    }
}

sealed class WorkoutEntry

data class HistoryEntry(var exerciseEntry: HistoryWrapper) : WorkoutEntry()

data class ExtendedEntry(val setEntry: ExtendedHistoryWrapper) : WorkoutEntry()

data class HistoryWrapper(
)

data class ExtendedHistoryWrapper(
)