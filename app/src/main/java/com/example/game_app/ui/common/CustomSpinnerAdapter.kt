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
    private val data: List<T>,
    private val listener: ItemSelectedListener<T>
) : SpinnerAdapter {

    private var selectedPosition: Int = 0
    private var isEnabled: Boolean = false

    override fun getCount(): Int = data.size

    override fun getItem(position: Int): T = data[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val textView = TextView(context)
        textView.text = data[position].toString()
        return textView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        TextView(context).let {
            it.text = data[position].toString()
            it.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            it.setPadding(16, 16, 16, 16)
            return it
        }
    }

    override fun getItemViewType(position: Int): Int = 0

    override fun getViewTypeCount(): Int = 1

    override fun hasStableIds(): Boolean = true

    override fun isEmpty(): Boolean = false

    override fun registerDataSetObserver(observer: DataSetObserver?) {
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver?) {
    }

    fun setItemSelectedListener(spinner: Spinner, position: Int, enabled: Boolean) {
        selectedPosition = position
        isEnabled = enabled

        spinner.adapter = this

        spinner.setSelection(selectedPosition)
        spinner.isEnabled = isEnabled
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedPosition = position
                listener.onItemSelected(data[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}