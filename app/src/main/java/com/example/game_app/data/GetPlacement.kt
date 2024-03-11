package com.example.game_app.data

object GetPlacement {
    fun findPlacement(players: Map<String, Int>, user: String) =
        getPlacementString(players.toList().sortedByDescending { (_, score) -> score }
            .indexOfFirst { (name, _) -> name == user } + 1)

    fun findPlacement(players: List<Pair<String, Int>>, user: String) =
        getPlacementString(players.sortedByDescending { (_, score) -> score }
            .indexOfFirst { (name, _) -> name == user } + 1)

    private fun getPlacementString(placement: Int): String {
        return when {
            placement % 10 == 1 && placement % 100 != 11 -> "$placement${"st"}"
            placement % 10 == 2 && placement % 100 != 12 -> "$placement${"nd"}"
            placement % 10 == 3 && placement % 100 != 13 -> "$placement${"rd"}"
            else -> "$placement${"th"}"
        }
    }
}