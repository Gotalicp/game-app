package com.example.game_app.ui.game.dialogs

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.game_app.databinding.DialogStartingInBinding

class StartingInDialogFragment(private val time: Long) : DialogFragment() {
    private var _binding: DialogStartingInBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogStartingInBinding.inflate(inflater, container, false)
        requireDialog().requestWindowFeature(Window.FEATURE_NO_TITLE)
        setStyle(STYLE_NO_FRAME, android.R.style.Theme)
        isCancelable = false
        requireDialog().window?.apply {
            setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        object : CountDownTimer(time, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(p0: Long) {
                binding.counter.text = "Starts in ${1+(p0 / 1000)}"
            }
            override fun onFinish() {
                dismiss()
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        const val TAG = "StartingIn"
    }
}