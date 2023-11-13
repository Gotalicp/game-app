package com.example.game_app.game.war

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.game_app.game.GameLogic

data class Play(
    val askPlayer: Int,
    val rank: Rank
)

class GameWarThread(override val playerNumber: Int,
                    override val roundSeed: Long,
                    override val players: List<Int>,
    ) : GameLogic<Play> {

    //Here i keep each players cards
    private lateinit var playersDeck : MutableList<MutableList<Card>>
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
        while (true){
            processTurn(t)
            //Takes the next player
            _playerToTakeTurn.postValue(players[players.indexOf(_playerToTakeTurn.value)+1 % players.size])
            //checks if move ended the game
            if (isGameEndConditionMet()) {
                endGame()
                break
            }
        }
    }
    private fun processTurn(play: Play ) {
        // Check if the asked player has any cards of the requested rank
        val rankToAskFor = play.rank
        val cardsReceived = playersDeck[play.askPlayer].filter { it.rank == rankToAskFor }
            if (cardsReceived.isNotEmpty()) {
                playersDeck[playerNumber].addAll(cardsReceived)
                playersDeck[play.askPlayer].removeAll(cardsReceived)

                // Check for books
                checkForBooks(currentPlayerHand)
                checkForBooks(askedPlayerHand)
            } else {
                // If no cards received, draw a card from the deck
                deck.drawCard()?.let {
                    playersDeck[]
                }
            }

        // Check for books in the current player's hand
        checkForBooks(currentPlayerHand)
    }
    }

    private fun checkForBooks(hand: MutableList<Card>) {
        val ranks = hand.groupBy { it.rank }
        for (rank in ranks.keys) {
            if (ranks[rank]?.size == 4) {
                println("Player ${_playerToTakeTurn.value} books $rank")
                hand.removeAll { it.rank == rank }
            }
        }
    }
    private fun isGameEndConditionMet(): Boolean {
        // Add logic to check if the game should end based on your rules
        // Update the isWinConditionMet variable accordingly
        return false // Change this condition based on your actual game logic
    }

}
