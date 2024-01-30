package com.example.game_app.ui.main.lobby

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.game_app.data.common.ItemClickListener
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
                itemClickListener = object : ItemClickListener<LobbyInfo> {
                    override fun onItemClicked(item: LobbyInfo, itemPosition: Int) {
                        Intent(context, GoFishActivity::class.java).let {
                            it.putExtra("lobbyUid", item.lobbyUid)
                            it.putExtra("lobbyIp", item.ownerIp)
                            startActivity(it)
                        }
                    }
                }
            }
            lobbiesView.apply {
                adapter = lobbiesRecycleView
                layoutManager = LinearLayoutManager(requireContext())
            }
            questionButton.setOnClickListener {
                Intent(context, GoFishActivity::class.java).let {
                    it.putExtra("lobbyUid", "prGaKzub0XS14sxCI12vNC6O6EN2")
                    it.putExtra("lobbyIp", "192.168.158.96")
                    startActivity(it)
                }
            }
            viewModel.lobbiesList.observe(viewLifecycleOwner) {
                lobbiesRecycleView.updateItems(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}