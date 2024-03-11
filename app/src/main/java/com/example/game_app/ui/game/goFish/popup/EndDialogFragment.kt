package com.example.game_app.ui.game.goFish.popup

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.game_app.data.GetPlacement
import com.example.game_app.databinding.DialogEndBinding
import com.example.game_app.domain.game.GoFishLogic
import com.example.game_app.ui.common.AppAcc

class EndDialogFragment(
    private val players: List<GoFishLogic.Player>,
    private val users: List<AppAcc>
) : DialogFragment() {
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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnExit.setOnClickListener {
                (context as? Activity)?.finish()
            }
            adapter.apply {
                updateItems(users.mapNotNull { name ->
                    players.find { it.uid == name.uid }?.let { player ->
                        EndScreenWrapper(
                            name.username,
                            player.score.toString(),
                            GetPlacement.findPlacement(players.map {
                                Pair(it.uid, it.score)
                            }, player.uid)
                        )
                    }
                }.sortedBy { it.score }
                )
            }
            scoreboard.layoutManager = LinearLayoutManager(context)
            scoreboard.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "End"
    }
}