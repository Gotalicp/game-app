package com.example.game_app.ui.game.dialogs.end

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.game_app.databinding.DialogEndBinding

class EndDialogFragment : DialogFragment() {
    private var _binding: DialogEndBinding? = null
    private val binding
        get() = requireNotNull(_binding)
    private val adapter = EndScreenAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEndBinding.inflate(inflater, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        isCancelable = false
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        );
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnExit.setOnClickListener {
                (context as? Activity)?.finish()
            }
            scoreboard.layoutManager = LinearLayoutManager(context)
            scoreboard.adapter = adapter
        }
    }

    fun updateView(list: List<EndWrapper>) {
        adapter.updateItems(list)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "End"
    }
}