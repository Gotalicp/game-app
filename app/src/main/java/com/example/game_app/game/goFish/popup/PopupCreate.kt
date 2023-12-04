package com.example.game_app.game.goFish.popup

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Spinner
import androidx.core.content.ContextCompat
import com.example.game_app.FireBaseViewModel
import com.example.game_app.R
import com.example.game_app.data.LobbyInfo
import com.example.game_app.game.goFish.GoFishViewModel

class PopupCreate(
    private var context: Context,
    private var viewModel : GoFishViewModel
){
    private val popupView: View

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupView = inflater.inflate(R.layout.window_create, null)
        popupView.setBackgroundColor(ContextCompat.getColor(context, androidx.appcompat.R.color.material_blue_grey_800))
        val items = arrayOf(3, 4, 5, 6)
        var selectedItem: Int = 0
        val spinner: Spinner = popupView.findViewById(R.id.spinner)
        val name = popupView.findViewById<EditText>(R.id.name)
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                selectedItem = items[position]
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }
    }

    fun showPopup(anchorView: View){
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        popupWindow.showAtLocation(
            anchorView,
            Gravity.CENTER,
            0,
            0
        )

        popupView.findViewById<Button>(R.id.btn_host).setOnClickListener {
            popupWindow.dismiss()
            viewModel.createGame(LobbyInfo())
        }
    }
}