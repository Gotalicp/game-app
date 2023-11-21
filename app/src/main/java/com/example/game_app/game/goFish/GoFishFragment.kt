package com.example.game_app.game.goFish

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.game_app.databinding.FragmentGoFishBinding

class GoFishFragment : Fragment() {
    private var _binding: FragmentGoFishBinding? = null
    private val binding get() = _binding!!

    private val goFishViewMode: GoFishViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentGoFishBinding.inflate(inflater, container, false)
        return binding.root
    }
}