package com.example.game_app.ui.main.profile

import  android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.game_app.databinding.FragmentProfileBinding
import com.example.game_app.ui.common.ItemClickListener

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding
    private val viewModel: ProfileViewModel by activityViewModels()

    private val historyAdapter = HistoryRecycleView()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            viewModel.acc.observe(viewLifecycleOwner) {
                profileImage.setImageBitmap(it.image)
                username.text = it.username
                email.text = viewModel.getEmail()
            }

            historyAdapter.apply {
                itemClickListener = object : ItemClickListener<HistoryWrapper>{
                    override fun onItemClicked(item: HistoryWrapper, itemPosition: Int) {
                     viewModel.onClick(itemPosition, item)
                    }
                }
            }
            history.layoutManager = LinearLayoutManager(context)
            history.adapter = historyAdapter
            viewModel.historyInfo.observe(viewLifecycleOwner){
                historyAdapter.updateItems(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}