package com.example.game_app.host

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.game_app.R
import com.example.game_app.SharedInformation
import com.example.game_app.TextRecycleView
import com.example.game_app.data.Messages
import com.example.game_app.databinding.FragmentHostBinding

class HostFragment : Fragment(R.layout.fragment_host) {
    private val hostViewModel:HostViewModel by activityViewModels()
    private val sharedChat: LiveData<MutableList<Messages>> = SharedInformation.getChat()

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
            SharedInformation.sharedChat.observe(viewLifecycleOwner){
                Log.d("observe", it.toString())
                textRecycleView.updateItems(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hostViewModel.end()
    }
}