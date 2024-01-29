package com.example.game_app.ui.login.login

import android.opengl.Visibility
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
import com.example.game_app.databinding.FragmentLoginBinding
import com.example.game_app.ui.game.goFish.GoFishUiMapper
import com.example.game_app.ui.game.goFish.GoFishUiModel
import com.example.game_app.ui.login.AuthenticationUIMapper
import com.example.game_app.ui.login.AuthenticationUiModel

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state.map { AuthenticationUIMapper.map(it) }
            .observe(viewLifecycleOwner) { updateContent(it) }

        binding?.apply {
            navigationRegister.setOnClickListener {
                findNavController().navigate(R.id.LoginToRegister)
            }
            btnLogin.setOnClickListener {
                if (password.text.toString() != "" && email.text.toString() != "") {
                    viewModel.logIn(
                        email.text.toString(),
                        password.text.toString()
                    )
                }
            }
            checkBoxShowPassword.setOnCheckedChangeListener { _, isChecked ->
                password.inputType = if (isChecked) {
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                } else {
                    android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
            }
        }
    }

    private fun updateContent(data: AuthenticationUiModel) {
        data.apply {
            if (success) {

            }
            binding?.loading?.visibility = if (isLoading) { VISIBLE } else { GONE }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}