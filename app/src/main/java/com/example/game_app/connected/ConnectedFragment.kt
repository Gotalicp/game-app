package com.example.game_app.connected

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.game_app.SharedInformation
import com.example.game_app.TextRecycleView
import com.example.game_app.data.LobbyInfo
import com.example.game_app.databinding.FragmentConnectedBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.InetAddress

class ConnectedFragment : Fragment() {
    private var _binding: FragmentConnectedBinding? = null
    private val binding get() = _binding!!

    private val connectedViewModel = ConnectedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConnectedBinding.inflate( inflater ,container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textRecycleView = TextRecycleView()
        connectedViewModel.join(arguments?.getString("ip")!!)
        _binding?.apply {
            textView.apply {
            adapter = textRecycleView
            layoutManager = LinearLayoutManager(requireContext())
        }
            btnSend.setOnClickListener {
                if(etMessage.text.isNotEmpty()){
                    GlobalScope.launch {
                        connectedViewModel.send(etMessage.text.toString())
                    }
                }
            }
            SharedInformation.sharedChat.observe(viewLifecycleOwner){
                Log.d("observe", it.toString())
                textRecycleView.updateItems(it)
            }
        }
    }
}