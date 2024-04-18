package com.example.game_app.ui.common

import android.view.View
import android.widget.AdapterView

class CustomItemSelectedListener(private var onItemSelectedAction: ((Int) -> Unit)?) :
    AdapterView.OnItemSelectedListener {

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        onItemSelectedAction?.invoke(position)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}
