package com.example.game_app.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.game_app.FireBaseViewModel
import com.example.game_app.R
import com.example.game_app.common.itemClickListener
import com.example.game_app.data.LobbyInfo
import com.example.game_app.databinding.FragmentMenuBinding

class MenuFragment : Fragment(R.layout.fragment_menu) {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FireBaseViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.apply {
            val lobbiesRecycleView = LobbiesRecycleView().apply {
                itemClickListener = object : itemClickListener<LobbyInfo> {
                    override fun onItemClicked(item: LobbyInfo, itemPosition: Int) {
                        val bundle = Bundle().apply {
                            putSerializable("lobbyInfo", item)
                        }
                        findNavController().navigate(R.id.MenuToConnected, bundle)
                    }
                }
            }
            lobbiesView.apply {
                adapter = lobbiesRecycleView
                layoutManager = LinearLayoutManager(requireContext())
            }
            questionButton.setOnClickListener{
                viewModel.refresh()
            }
            viewModel.lobbiesList.observe(viewLifecycleOwner){
                lobbiesRecycleView.updateItems(it)
            }
        }
    }
}