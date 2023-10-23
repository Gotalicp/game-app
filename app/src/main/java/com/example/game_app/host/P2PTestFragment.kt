package com.example.game_app.host

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.game_app.R
import com.example.game_app.databinding.FragmentP2PTestBinding

class P2PTestFragment : Fragment(R.layout.fragment_p2_p_test) {
    private var _binding: FragmentP2PTestBinding? = null
    private val binding get() = _binding!!

    private val viewModel: P2PTestViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentP2PTestBinding.inflate(inflater, container, false)
        _binding!!.apply {
            btnDiscover.setOnClickListener {
                viewModel.discoverPeers()
            }
            btnSend.setOnClickListener {
                val message = etMessage.text.toString()
                if (message.isNotEmpty()) {
                    viewModel.connectAndSendMessage(message)
                }
            }
            viewModel.isWifiP2pEnabled.observe(viewLifecycleOwner) { isEnabled ->
            }

            viewModel.peers.observe(viewLifecycleOwner) { peers ->
            }

            viewModel.connectionInfo.observe(viewLifecycleOwner) { info ->
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}