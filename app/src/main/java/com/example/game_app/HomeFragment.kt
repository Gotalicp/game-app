package com.example.game_app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.game_app.databinding.FragmentHomeBinding
import com.example.game_app.game.goFish.GoFishActivity
import com.example.game_app.login.AuthenticationActivity
import com.example.game_app.login.ui.login.AuthenticationViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val auth: AuthenticationViewModel by activityViewModels()

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
            btnLibrary.setOnClickListener {
                findNavController().navigate(R.id.HomeToLibrary)
            }
            btnHost.setOnClickListener{
                startActivity(Intent(context, GoFishActivity::class.java))
            }
            btnSettings.setOnClickListener {
//                findNavController().navigate(R.id.HomeToSettings)
                auth.logout()
            }
        }
    }
}