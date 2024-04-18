package com.example.game_app.ui.game.dialogs.lobby

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.game_app.databinding.DialogLobbyBinding
import com.example.game_app.domain.LobbyProvider
import com.example.game_app.ui.common.CustomItemSelectedListener
import com.example.game_app.ui.common.CustomSpinnerAdapter

class LobbyDialogFragment(
    private val canChangeSettings: Boolean,
    private val startGame: (() -> Unit)?,
    private val maxPlayers: List<Int>,
    private val timeLimit: List<String>,
    private val rounds: List<Int>
) : DialogFragment() {
    private var _binding: DialogLobbyBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    private var canStart = false

    private val viewModel: LobbyViewModel by viewModels()
    private val adapter = LobbyAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogLobbyBinding.inflate(inflater, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        isCancelable = false
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnStart.apply {
                visibility = if (canChangeSettings) View.VISIBLE else View.GONE
                setOnClickListener {
                    if (canStart) startGame?.invoke()
                }
            }
            btnExit.setOnClickListener {
                (context as? Activity)?.finish()
            }

            playerRecycleView.layoutManager = LinearLayoutManager(context)
            playerRecycleView.adapter = adapter

            LobbyProvider.getLobby().observe(context as LifecycleOwner) { lobbyInfo ->
                if (lobbyInfo.code == "") {
                    (context as? Activity)?.finish()
                }
                lobbyCode.text = lobbyInfo.code
                canStart = (lobbyInfo.players.size >= 2)
                turnTimeLimit.setSelection(timeLimit.indexOf(lobbyInfo.secPerTurn))
                playerLimit.setSelection(maxPlayers.indexOf(lobbyInfo.maxPlayerCount))
                roundLimit.setSelection(rounds.indexOf(lobbyInfo.rounds))
                gameMode.text = "Game mode: ${lobbyInfo.clazz}"
                viewModel.getPlayer(lobbyInfo) {
                    adapter.updateItems(it)
                }
            }

            turnTimeLimit.apply {
                setSelection(0)
                isEnabled = canChangeSettings
                adapter = CustomSpinnerAdapter(context, timeLimit)
                onItemSelectedListener = CustomItemSelectedListener {
                    viewModel.changeSettings(canChangeSettings, time = timeLimit[it])
                }
            }

            playerLimit.apply {
                setSelection(0)
                isEnabled = canChangeSettings
                adapter = CustomSpinnerAdapter(context, maxPlayers)
                onItemSelectedListener = CustomItemSelectedListener {
                    viewModel.changeSettings(canChangeSettings, playerLimit = maxPlayers[it])
                }
            }

            roundLimit.apply {
                setSelection(0)
                isEnabled = canChangeSettings
                adapter = CustomSpinnerAdapter(context, rounds)
                onItemSelectedListener = CustomItemSelectedListener {
                    viewModel.changeSettings(canChangeSettings, rounds = rounds[it])
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