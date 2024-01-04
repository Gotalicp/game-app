package com.example.game_app.ui.main.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.navigateUp
import com.example.game_app.R
import com.example.game_app.databinding.FragmentHomeBinding
import com.example.game_app.ui.game.goFish.GoFishActivity
import com.example.game_app.ui.login.AuthenticationViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private val auth: AuthenticationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.apply {
            btnHost.setOnClickListener{
                startActivity(Intent(context, GoFishActivity::class.java))
            }
            btnPlay.setOnClickListener {
                findNavController().navigate(R.id.homeToLobby)
            }
        }
    }
}