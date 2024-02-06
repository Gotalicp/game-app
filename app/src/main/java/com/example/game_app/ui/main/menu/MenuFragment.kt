package com.example.game_app.ui.main.menu

import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.example.game_app.R
import com.example.game_app.data.Account
import com.example.game_app.data.LibraryGame
import com.example.game_app.data.common.ItemClickListener
import com.example.game_app.databinding.FragmentMenuBinding
import com.example.game_app.ui.game.goFish.GoFishActivity
import com.example.game_app.ui.game.goFish.GoFishLogic
import com.example.game_app.ui.game.goFish.PlayersRecycleView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MenuFragment : Fragment(R.layout.fragment_menu) {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding

    private val viewModel: MenuViewModel by viewModels()

    private val viewAdapter = MenuRecycleViewAdapter()

    private val list = listOf(
        LibraryGame(
            R.drawable.image, GoFishActivity::class.java,
            "Five cards are dealt from a standard 52-card deck to each player. The remaining cards are shared between the players, usually spread out in a disorderly pile referred to as the \"ocean\" or \"pool\". The player whose turn it is to play asks any another player for their cards of a particular face value."
        ))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            viewAdapter.apply {
                joinListener = object : ItemClickListener<Class<*>> {
                    override fun onItemClicked(item: Class<*>, itemPosition: Int) {
                        codeLayout.visibility = View.VISIBLE
                        codeText.requestFocus()
                        codeText.apply {
                            setOnFocusChangeListener { _, focus ->
                                if (!focus) {
                                    codeLayout.visibility = View.GONE
                                }
                            }
                            setOnKeyListener { _, keyCode, event ->
                                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                                    viewModel.join(text.toString(), item)
                                    clearFocus()
                                    true
                                } else {
                                    false
                                }
                            }
                        }
                    }
                }

                hostListener = object : ItemClickListener<Class<*>> {
                    override fun onItemClicked(item: Class<*>, itemPosition: Int) {
                        startActivity(viewModel.host(item))
                    }
                }
            }
            recyclerViewLibrary.adapter = viewAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}