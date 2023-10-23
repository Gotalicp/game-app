package com.example.game_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.game_app.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.apply {
            btnPlay.setOnClickListener {
                findNavController().navigate(R.id.HomeToMenu)
            }
            btnHost.setOnClickListener {
                findNavController().navigate(R.id.HomeToP2P)
            }
            btnLibrary.setOnClickListener {
                findNavController().navigate(R.id.HomeToLibrary)
            }
            btnSettings.setOnClickListener {
                findNavController().navigate(R.id.HomeToSettings)
            }
        }
    }
}