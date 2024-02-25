package com.example.game_app.ui.game.goFish.popup

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.game_app.data.PlayerCache
import com.example.game_app.databinding.DialogLobbyBinding
import com.example.game_app.domain.LobbyProvider
import com.example.game_app.domain.SharedInformation
import com.example.game_app.ui.common.CustomSpinnerAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LobbyDialogFragment(
    private val canChangeSettings: Boolean,
    private val startGame: (() -> Unit)?
) : DialogFragment() {
    private var _binding: DialogLobbyBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    private val viewModel: LobbyViewModel by viewModels()
    private val cache = PlayerCache.instance
    private val adapter = LobbyAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogLobbyBinding.inflate(inflater, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        isCancelable = false
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnStart.apply {
                visibility = if (canChangeSettings) View.VISIBLE else View.GONE
                setOnClickListener {
                    dismiss()
                    startGame?.invoke()
                }
            }
            btnExit.setOnClickListener {
                dismiss()
                (context as? Activity)?.finish()
            }

            playerRecycleView.layoutManager = LinearLayoutManager(context)
            playerRecycleView.adapter = adapter

            LobbyProvider.getLobby().observe(context as LifecycleOwner) { lobbyInfo ->
                lobbyCode.text = lobbyInfo.code
                CoroutineScope(Dispatchers.Main).launch(Dispatchers.Main) {
                    adapter.updateItems(lobbyInfo.players.mapNotNull { cache.get(it) })
                }
            }

            turnTimeLimit.apply {
                listOf("No limit", "15", "30", "45", "60").let {
                    setSelection(0)
                    isEnabled = canChangeSettings
                    adapter = CustomSpinnerAdapter(context, it)
                    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            viewModel.changeTime(canChangeSettings, time = it[position])
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                    }
                }
            }
            playerLimit.apply {
                listOf(2, 3, 4, 5, 6).let {
                    setSelection(0)
                    isEnabled = canChangeSettings
                    adapter = CustomSpinnerAdapter(context, it)
                    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            viewModel.changeTime(canChangeSettings, playerLimit = it[position])
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                    }
                }
            }
            roundLimit.apply {
                listOf(2, 3, 4, 5, 6).let {
                    setSelection(0)
                    isEnabled = canChangeSettings
                    adapter = CustomSpinnerAdapter(context, it)
                    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            viewModel.changeTime(canChangeSettings, rounds = it[position])
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "Lobby"
    }
}