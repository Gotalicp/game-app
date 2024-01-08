package com.example.game_app.ui.main.lobby

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.game_app.data.common.itemClickListener
import com.example.game_app.data.LobbyInfo
import com.example.game_app.databinding.FragmentLobbyBinding
import com.example.game_app.ui.game.goFish.GoFishActivity

class LobbyFragment : Fragment() {
    private var _binding: FragmentLobbyBinding? = null
    private val binding get() = _binding

    private val viewModel: LobbyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLobbyBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.apply {
            val lobbiesRecycleView = LobbiesRecycleView().apply {
                itemClickListener = object : itemClickListener<LobbyInfo> {
                    override fun onItemClicked(item: LobbyInfo, itemPosition: Int) {
                        val intent = Intent(context, GoFishActivity::class.java)
                        intent.putExtra("lobbyUid", item.lobbyUid)
                        intent.putExtra("lobbyIp", item.ownerIp)
                        startActivity(intent)
                    }
                }
            }
            lobbiesView.apply {
                adapter = lobbiesRecycleView
                layoutManager = LinearLayoutManager(requireContext())
            }
            questionButton.setOnClickListener {
                viewModel.checkLocalServers()
            }
            viewModel.lobbiesList.observe(viewLifecycleOwner) {
                lobbiesRecycleView.updateItems(it)
            }
//            viewModel.checkLocalServers()
        }
    }
}