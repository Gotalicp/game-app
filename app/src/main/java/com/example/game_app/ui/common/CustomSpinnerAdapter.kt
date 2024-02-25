package com.example.game_app.ui.common

import android.content.Context
import android.database.DataSetObserver
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.SpinnerAdapter
import android.widget.TextView

class CustomSpinnerAdapter<T>(
    private val context: Context,
    private val data: List<T>) : SpinnerAdapter {
    override fun getCount(): Int = data.size

    override fun getItem(position: Int): T = data[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?) =
        TextView(context).apply {
            text = data[position].toString()
        }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?) =
        TextView(context).apply {
            text = data[position].toString()
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(16, 16, 16, 16)
        }

    override fun getItemViewType(position: Int): Int = 0

    override fun getViewTypeCount(): Int = 1

    override fun hasStableIds(): Boolean = true

    override fun isEmpty(): Boolean = false

    override fun registerDataSetObserver(observer: DataSetObserver?) {
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver?) {
    }
}