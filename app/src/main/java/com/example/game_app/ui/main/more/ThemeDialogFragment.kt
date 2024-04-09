package com.example.game_app.ui.main.more

import android.content.SharedPreferences
import com.example.game_app.R
import com.example.game_app.databinding.DialogThemeBinding
import com.example.game_app.ui.main.MainActivity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.print.PrintAttributes.Margins
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.game_app.data.SharedTheme

class ThemeDialogFragment : DialogFragment() {
    private var _binding: DialogThemeBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    private lateinit var sharedPreferences: SharedTheme

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogThemeBinding.inflate(inflater, container, false)
        sharedPreferences = SharedTheme(requireActivity())
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setStyle(STYLE_NO_FRAME, android.R.style.Theme)
        dialog?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lightTheme.setOnClickListener {
                sharedPreferences.saveTheme(R.style.AppTheme_Light)
                (requireActivity() as MainActivity).recreate()
                dismiss()
            }

            blueTheme.setOnClickListener {
                sharedPreferences.saveTheme(R.style.AppTheme_Blue)
                (requireActivity() as MainActivity).recreate()
                dismiss()
            }

            colorfulTheme.setOnClickListener {
                sharedPreferences.saveTheme(R.style.AppTheme_Colorful)
                (requireActivity() as MainActivity).recreate()
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "Theme"
    }
}