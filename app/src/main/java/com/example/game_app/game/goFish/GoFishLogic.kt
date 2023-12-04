package com.example.game_app.game.goFish

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.game_app.common.GameLogic
import com.example.game_app.data.PlayerInfo
import com.example.game_app.game.Card
import com.example.game_app.game.Deck
import com.example.game_app.game.Rank
import java.io.Serializable
import java.util.Random

data class Play(
    val askingPlayer: String,
    val askedPlayer: String,
    val rank: Rank
):Serializable

class GoFishLogic : GameLogic<Play> {
    data class Player(
        val deck: MutableList<Card>,
        val info: PlayerInfo,
        var score: Int
    )

    //all the players
    private val _gamePlayers = MutableLiveData<MutableList<Player>>()
    val gamePlayers: LiveData<MutableList<Player>> get() = _gamePlayers
    //Checking who's turn is it
    private val _playerToTakeTurn = MutableLiveData<Player>()
    val playerToTakeTurn: LiveData<Player> get() = _playerToTakeTurn

    //initiate deck
    private var deck = Deck()
    override fun startGame(seed: Long,players: MutableList<PlayerInfo>) {
        deck.showDeck()
        deck.shuffle(seed)
        //create players
        _gamePlayers.value = players.map {
            Player(mutableListOf(), it, 0)
        }.toMutableList()
        //randomizes game turns
        _gamePlayers.value!!.shuffle(Random(seed))
        //sets first player to take turn
        _playerToTakeTurn.postValue(gamePlayers.value!![0])
        //gives cards to players
        _gamePlayers.value = deck.deal(players,5, deck)
        deck.showDeck()
    }
    override fun endGame() {
        TODO()
    }
    override fun turnHandling(t: Play ) {
        // Check if the asked player has any cards of the requested rank
        gamePlayers.value!!.let {gamePlayer->
            val cardsReceived = gamePlayer[indexOf(t.askedPlayer)].deck.filter { it.rank == t.rank }
            if (cardsReceived.isNotEmpty()) {
                gamePlayer[indexOf(t.askingPlayer)].deck.addAll(cardsReceived)
                gamePlayer[indexOf(t.askedPlayer)].deck.removeAll(cardsReceived)
                checkForEmptyHand(gamePlayer[indexOf(t.askedPlayer)])
            } else {
                // If no cards received, draw a card from the deck
                deck.drawCard()?.let {
                    gamePlayer[indexOf(t.askingPlayer)].deck.add(it)
                }
            }
            // Check for books in the current player's hand
            if (checkForBooks(gamePlayer[indexOf(t.askingPlayer)])) {
                // Sets next player
                _playerToTakeTurn.postValue(gamePlayer[(indexOf(t.askingPlayer) + 1) % gamePlayer.size])
            }
            if (gameEnded()) {
                endGame()
            }
        }
    }

    private fun checkForBooks(hand: Player):Boolean{
        val ranks = hand.deck.groupBy { it.rank }
        for (rank in ranks.keys) {
            if (ranks[rank]?.size == 4) {
                Log.d("CheckBook","Player ${_playerToTakeTurn.value} books $rank")
                hand.deck.removeAll { it.rank == rank }
                hand.score+=1
                checkForEmptyHand(hand)
                return false
            }
        }
        return true
    }
    private fun checkForEmptyHand(hand: Player){
        if(hand.deck.isEmpty()){
            deck.drawCard()?.let { hand.deck.add(it) }
        }
    }

    override fun gameEnded() = (deck.isEmpty() && gamePlayers.value!!.all { it.deck.isEmpty() })
    private fun indexOf(uid : String) = gamePlayers.value!!.indexOfFirst { it.info.uid == uid}
}
