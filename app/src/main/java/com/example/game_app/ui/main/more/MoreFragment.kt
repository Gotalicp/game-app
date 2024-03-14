package com.example.game_app.ui.main.more

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.game_app.data.clickWithDebounce
import com.example.game_app.data.firebase.FireBaseUtilityAcc
import com.example.game_app.databinding.FragmentMoreBinding
import com.example.game_app.domain.bitmap.UriToBitmap

class MoreFragment : Fragment() {
    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding

    private val viewModel: MoreViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            logout.setOnClickListener {
                FireBaseUtilityAcc.logout()
            }
            policy.setOnClickListener {
                PolicyDialogFragment().show(
                    childFragmentManager,
                    PolicyDialogFragment.TAG
                )
            }
            changeImage.clickWithDebounce {
                Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "image/*"
                    resultLauncher.launch(this)
                }
            }

            changeName.setOnClickListener {
                RenameDialogFragment().show(
                    childFragmentManager,
                    RenameDialogFragment.TAG
                )
            }
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (context != null && result.data?.data != null) {
                    viewModel.updateUser(image = UriToBitmap(requireContext()).adapt(result.data?.data!!))
                }
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}