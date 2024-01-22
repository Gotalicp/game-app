package com.example.game_app.ui.login.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import com.example.game_app.R
import com.example.game_app.databinding.FragmentRegisterBinding
import com.example.game_app.ui.login.AuthenticationUIMapper
import com.example.game_app.ui.login.AuthenticationUiModel

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding?=null
    private val binding get() = _binding

    private val viewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state.map { AuthenticationUIMapper.map(it) }
            .observe(viewLifecycleOwner) { updateContent(it) }

        binding?.apply {
            navigationLogin.setOnClickListener {
                findNavController().navigate(R.id.RegisterToLogin)
            }
            btnSignIn.setOnClickListener {
                if (password.text.toString() != ""
                    && repassword.text.toString() == password.text.toString()
                    && email.text.toString() != ""
                    && password.text.toString() != "" ) {
                    viewModel.createAcc(
                        usenname.text.toString(),
                        email.text.toString(),
                        password.text.toString(),
                        requireContext())
                }
            }
            checkBoxShowPassword.setOnCheckedChangeListener{ _, isChecked ->
                password.inputType = if (isChecked) {
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                } else {
                    android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
                repassword.inputType = password.inputType
            }
        }
    }
    private fun updateContent(data: AuthenticationUiModel) {
        data.apply {
            if (failed) {

            }
            binding?.loading?.visibility = if (isLoading) { VISIBLE } else { GONE }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}