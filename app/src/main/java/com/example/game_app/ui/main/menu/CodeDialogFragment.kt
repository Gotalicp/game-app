package com.example.game_app.ui.main.menu

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.game_app.databinding.DialogEnterCodeBinding

class CodeDialogFragment(private val clazz: Class<*>) : DialogFragment() {
    private var _binding: DialogEnterCodeBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    private val viewModel: MenuViewModel by viewModels({ requireActivity() })
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEnterCodeBinding.inflate(inflater, container, false)
        requireDialog().requestWindowFeature(Window.FEATURE_NO_TITLE)
        setStyle(STYLE_NO_FRAME, android.R.style.Theme)
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
        binding.apply {
            btnClose.setOnClickListener {
                dismiss()
            }
            btnJoin.setOnClickListener {
                viewModel.join(codeEdit.text.toString(), clazz) {
                    it?.let {
                        startActivity(it)
                        dialog?.dismiss()
                    }
                }
            }
            codeEdit.apply {
                setOnEditorActionListener { _, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_DONE ||
                        (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)
                    ) {
                        viewModel.join(text.toString(), clazz) {
                            it?.let {
                                startActivity(it)
                                dialog?.dismiss()
                            }
                        }
                        return@setOnEditorActionListener true
                    }
                    false
                }
                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
                            this,
                            InputMethodManager.SHOW_IMPLICIT
                        )
                    } else {
                        (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                            .hideSoftInputFromWindow(
                                this.windowToken,
                                0
                            )
                    }
                }
                requestFocus()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "EnterCode"
    }
}