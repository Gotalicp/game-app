package com.example.game_app.ui.game.dialogs.end

import com.example.game_app.data.GetPlacement
import com.example.game_app.data.common.Adapter
import com.example.game_app.domain.game.PlayerWrapper
import com.example.game_app.ui.common.AppAcc

class PlayerLeaderBoardAdapter :
    Adapter<Pair<List<AppAcc>, List<PlayerWrapper>>, List<EndWrapper>> {
    override fun adapt(t: Pair<List<AppAcc>, List<PlayerWrapper>>) = t.first.mapNotNull { name ->
        t.second.find { it.uid == name.uid }?.let { player ->
            EndWrapper(
                name.username,
                player.score.toString(),
                GetPlacement.findPlacement(t.second.map {
                    Pair(it.uid, it.score)
                }, player.uid)
            )
        }
    }.sortedByDescending { it.score }
}