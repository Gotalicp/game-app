package com.example.game_app.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.game_app.data.common.GenericDiffUtil

abstract class RecycleViewAdapter<T>(
    private val areItemsTheSame: (oldItem: T, newItem: T) -> Boolean,
    private val areContentsTheSame: (oldItem: T, newItem: T) -> Boolean,
    private val layoutResId: Int,
) : RecyclerView.Adapter<RecycleViewAdapter<T>.BaseViewHolder>() {

    private val items = ArrayList<T>()
    private var diffUtil: GenericDiffUtil<T> = GenericDiffUtil(
        oldList = listOf(),
        newList = listOf(),
        areItemsTheSame = areItemsTheSame,
        areContentsTheSame = areContentsTheSame
    )

    abstract fun createViewHolder(view: View): BaseViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return createViewHolder(
            LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<T>) {
        DiffUtil.calculateDiff(
            GenericDiffUtil(
                oldList = items,
                newList = newItems,
                areItemsTheSame = areItemsTheSame,
                areContentsTheSame = areContentsTheSame
            ).apply {
                items.clear()
                items.addAll(newItems)
                diffUtil = this
            }
        ).dispatchUpdatesTo(this)
    }

    open inner class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        open fun bind(item: T) {

        }
    }

    internal fun getItems() = items
}