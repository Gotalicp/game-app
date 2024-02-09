package com.example.game_app.ui.main.menu

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.game_app.databinding.DialogEnterCodeBinding
import kotlin.math.round

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
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnClose.setOnClickListener {
                dismiss()
            }
            btnJoin.setOnClickListener {
                if (codeEdit.text.length == 6) {
                    viewModel.join(text.toString(), clazz)?.let {
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
                        if (text.length == 6) {
                            viewModel.join(text.toString(), clazz)?.let {
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