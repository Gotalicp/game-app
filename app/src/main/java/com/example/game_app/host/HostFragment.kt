package com.example.game_app.host

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.game_app.R
import com.example.game_app.TextRecycleView
import com.example.game_app.databinding.FragmentHostBinding


class HostFragment : Fragment(R.layout.fragment_host) {
    private var hostViewModel  = HostViewModel()

    private var _binding: FragmentHostBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hostViewModel.start()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textRecycleView = TextRecycleView()
        _binding?.apply {
            textView.apply {
                adapter = textRecycleView
                layoutManager = LinearLayoutManager(requireContext())
            }
            btnSend.setOnClickListener {
                if(etMessage.text.isNotEmpty()){
                    hostViewModel.send(etMessage.text.toString())
                }
            }
            hostViewModel.messages.observe(viewLifecycleOwner){
                textRecycleView.updateItems(it)
            }
        }
    }
}