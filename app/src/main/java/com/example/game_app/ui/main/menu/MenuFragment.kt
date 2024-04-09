package com.example.game_app.ui.main.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.game_app.R
import com.example.game_app.ui.common.ItemClickListener
import com.example.game_app.databinding.FragmentMenuBinding
import com.example.game_app.ui.game.chess.ChessActivity
import com.example.game_app.ui.game.coin.CoinActivity
import com.example.game_app.ui.game.goFish.GoFishActivity

class MenuFragment : Fragment(R.layout.fragment_menu), MenuAdapter.AdapterListener {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding

    private val viewModel: MenuViewModel by viewModels()

    private val list = listOf(
        LibraryGame(
            R.drawable.go_fish, GoFishActivity::class.java, R.string.go_fish_description
        ),
        LibraryGame(
            R.drawable.chess, ChessActivity::class.java, R.string.chess_description
        ),
        LibraryGame(
            R.drawable.coin_flip, CoinActivity::class.java,R.string.coin_flip_description
        )
    )
    private var isVerticalScrollEnabled = true

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
            recyclerViewLibrary.adapter =
                MenuAdapter(callback = this@MenuFragment).apply {
                    updateItems(list)
                    joinListener = object : ItemClickListener<Class<*>> {
                        override fun onItemClicked(item: Class<*>, itemPosition: Int) {
                            CodeDialogFragment(item).show(
                                childFragmentManager,
                                CodeDialogFragment.TAG
                            )
                        }
                    }


                    hostListener = object : ItemClickListener<Class<*>> {
                        override fun onItemClicked(item: Class<*>, itemPosition: Int) {
                            startActivity(viewModel.host(item))
                        }
                    }
                    recyclerViewLibrary.layoutManager = object : LinearLayoutManager(context) {
                        override fun canScrollVertically(): Boolean {
                            return isVerticalScrollEnabled
                        }
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onScrollViewTouched(isTouched: Boolean) {
        isVerticalScrollEnabled = !isTouched
    }
}