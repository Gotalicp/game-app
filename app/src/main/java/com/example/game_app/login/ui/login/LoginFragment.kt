package com.example.game_app.login.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.game_app.R
import com.example.game_app.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding
    private val authenticationViewModel: AuthenticationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            navigationRegister.setOnClickListener {
                findNavController().navigate(R.id.LoginToRegister)
            }
            btnLogin.setOnClickListener {
                if (password.text.toString() != "" && email.text.toString() != "") {
                    authenticationViewModel.logIn(
                        email.text.toString(),
                        password.text.toString()
                    )
                }
            }
            checkBoxShowPassword.setOnCheckedChangeListener{ _, isChecked ->
                password.inputType = if (isChecked) {
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                } else {
                    android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
            }
        }
    }
}