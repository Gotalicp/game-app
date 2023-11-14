package com.example.game_app.game.war

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.game_app.game.war.Deck
import com.example.game_app.game.GameLogic

data class Play(
    val askingPlayer: Int,
    val askedPlayer: Int,
    val rank: Rank
)

class GameWarThread(override val playerNumber: Int,
                    override val roundSeed: Long,
                    override val players: List<Int>,
    ) : GameLogic<Play> {

    //Here i keep each players cards
    private lateinit var playersDeck : MutableList<Pair<MutableList<Card>, Int>>
    //Checking who's turn is it
    private val _playerToTakeTurn = MutableLiveData<Int>()
    val playerToTakeTurn: LiveData<Int> get() = _playerToTakeTurn

    //initiate deck
    private var deck = Deck()
    override fun startGame() {
        _playerToTakeTurn.postValue(1)
        deck.showDeck()
        deck.shuffle(roundSeed)
        playersDeck = deck.deal(players,5, deck)
        deck.showDeck()
    }
    override fun endGame() {
    }


    override fun turnHandling(t: Play) {
            processTurn(t)
            //Takes the next player
            _playerToTakeTurn.postValue(players[players.indexOf(_playerToTakeTurn.value)+1 % players.size])
            //checks if move ended the game
    }
    private fun processTurn(play: Play ) {
        // Check if the asked player has any cards of the requested rank
        val cardsReceived = playersDeck[play.askedPlayer].first.filter { it.rank == play.rank }
            if (cardsReceived.isNotEmpty()) {
                playersDeck[play.askingPlayer].first.addAll(cardsReceived)
                playersDeck[play.askedPlayer].first.removeAll(cardsReceived)
            } else {
                // If no cards received, draw a card from the deck
                deck.drawCard()?.let {
                    playersDeck[playerNumber].first.add(it)
                }
            }
        // Check for books in the current player's hand
        checkForBooks(playersDeck[play.askingPlayer].first)
    }

    private fun checkForBooks(hand: MutableList<Card>) {
        val ranks = hand.groupBy { it.rank }
        for (rank in ranks.keys) {
            if (ranks[rank]?.size == 4) {
                Log.d("CheckBook","Player ${_playerToTakeTurn.value} books $rank")
                hand.removeAll { it.rank == rank }
            }
        }
    }

    override fun gameEnded(): Boolean {
        TODO("Not yet implemented")
        return false
    }
}
