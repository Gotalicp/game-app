package com.example.game_app.login.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.game_app.R
import com.example.game_app.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding?=null
    private val binding get() = _binding!!

    private val authenticationViewModel: AuthenticationViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            navigationLogin.setOnClickListener {
                findNavController().navigate(R.id.RegisterToLogin)
            }
            btnSignIn.setOnClickListener {
                if (password.text.toString() != "" && repassword.text.toString() == password.text.toString() && email.text.toString() != "") {
                    authenticationViewModel.createAcc(
                        email.text.toString(),
                        password.text.toString(),
                        view
                    )
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
}