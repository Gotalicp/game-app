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

class GoFishLogic(override val players: MutableList<PlayerInfo>): GameLogic<Play> {
    data class Player(
        val deck: MutableList<Card>,
        val info: PlayerInfo,
        var score: Int
    )
    //Here i keep each players cards
    private lateinit var gamePlayer : MutableList<Player>
    //Checking who's turn is it
    private val _playerToTakeTurn = MutableLiveData<Player>()
    val playerToTakeTurn: LiveData<Player> get() = _playerToTakeTurn


    //initiate deck
    private var deck = Deck()
    override fun startGame(seed: Long) {
        deck.showDeck()
        deck.shuffle(seed)
        //randomizes game turns
        gamePlayer.shuffle(Random(seed))
        _playerToTakeTurn.postValue(gamePlayer[0])
        gamePlayer = deck.deal(players,5, deck)
        deck.showDeck()
    }
    override fun endGame() {
        TODO()
    }
    override fun turnHandling(t: Play ) {
        // Check if the asked player has any cards of the requested rank
        val cardsReceived = gamePlayer[indexOf(t.askedPlayer)].deck.filter { it.rank == t.rank }
        if (cardsReceived.isNotEmpty()) {
            gamePlayer[indexOf(t.askingPlayer)].deck.addAll(cardsReceived)
            gamePlayer[indexOf(t.askedPlayer)].deck.removeAll(cardsReceived)
            checkForEmptyHand(gamePlayer[indexOf(t.askedPlayer)])
        } else {
            //sets next player
            _playerToTakeTurn.postValue(gamePlayer[(indexOf(t.askingPlayer)+1)%gamePlayer.size])
            // If no cards received, draw a card from the deck
            deck.drawCard()?.let {
                gamePlayer[indexOf(t.askingPlayer)].deck.add(it)
            }
        }
        // Check for books in the current player's hand
        checkForBooks(gamePlayer[indexOf(t.askingPlayer)])
        if(gameEnded()){ endGame() }
    }

    private fun checkForBooks(hand: Player) {
        val ranks = hand.deck.groupBy { it.rank }
        for (rank in ranks.keys) {
            if (ranks[rank]?.size == 4) {
                Log.d("CheckBook","Player ${_playerToTakeTurn.value} books $rank")
                hand.deck.removeAll { it.rank == rank }
                hand.score+=1
                checkForEmptyHand(hand)
            }
        }
    }
    private fun checkForEmptyHand(hand: Player){
        if(hand.deck.isEmpty()){
            deck.drawCard()?.let { hand.deck.add(it) }
        }
    }

    override fun gameEnded() = (deck.isEmpty() && gamePlayer.all { it.deck.isEmpty() })
    private fun indexOf(uid : String) = gamePlayer.indexOfFirst { it.info.uid == uid}
}
