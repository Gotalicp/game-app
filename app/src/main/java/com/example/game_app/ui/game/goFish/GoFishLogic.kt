package com.example.game_app.ui.game.goFish

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.game_app.data.GameLogic
import com.example.game_app.data.SharedInformation
import com.example.game_app.domain.Card
import com.example.game_app.domain.Deck
import com.example.game_app.domain.Rank
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.Serializable
import java.util.Random

data class Play(
    val askingPlayer: String,
    val askedPlayer: String,
    val rank: Rank
) : Serializable

class GoFishLogic : GameLogic<Play> {
    data class Player(
        val deck: MutableList<Card>,
        val uid: String,
        var score: Int
    ) : Serializable

    //All the players
    private val _gamePlayers = MutableLiveData<MutableList<Player>>()
    val gamePlayers: LiveData<MutableList<Player>> get() = _gamePlayers

    //Checking who's turn is it
    private val _playerToTakeTurn = MutableLiveData<Player>()
    val playerToTakeTurn: LiveData<Player> get() = _playerToTakeTurn

    private val _hasEnded = MutableStateFlow(false)
    val hasEnded: StateFlow<Boolean> get() = _hasEnded

    private val _seed = MutableStateFlow<Long?>(null)
    val seed: StateFlow<Long?> get() = _seed

    //Initiate deck
    private var deck = Deck()
    private fun resetDeck() = Deck()
    fun getDeckSize() = deck.deckSize()
    override fun setPlayer(players: MutableList<String>) {
        _gamePlayers.value = players.map { Player(mutableListOf(), it, 0) }.toMutableList()
    }

    override fun updateSeed(seed: Long) {
        _seed.value = seed
    }

    override fun startGame(seed: Long) {
        if (_gamePlayers.value == null) {
            SharedInformation.getLobby().value?.players?.let { setPlayer(it) }
        }
        _hasEnded.value = false
        deck = resetDeck()
        deck.shuffle(seed)
        deck.showDeck()
        if (!_gamePlayers.value.isNullOrEmpty()) {
            _gamePlayers.value?.let {
                it.shuffle(Random(seed))
                _gamePlayers.postValue(it)
                _gamePlayers.postValue(deck.deal(it, 5, deck))
                _playerToTakeTurn.postValue(it[0])
            }
        }
    }

    override fun turnHandling(t: Play) {
        // Check if the asked player has any cards of the requested rank
        _gamePlayers.value?.let { player ->
            val cardsReceived = player[indexOf(t.askedPlayer)].deck.filter { it.rank == t.rank }
            if (cardsReceived.isNotEmpty()) {
                player.find { it.uid == t.askingPlayer }?.deck?.addAll(cardsReceived)
                player.find { it.uid == t.askedPlayer }?.deck?.removeAll(cardsReceived)
                player.find { it.uid == t.askedPlayer }?.let { checkForEmptyHand(it) }
                player.find { it.uid == t.askingPlayer }?.let { checkForBooks(it) }
                _playerToTakeTurn.postValue(player[indexOf(t.askingPlayer)])
            } else {
                // If no cards received, draw a card from the deck
                deck.drawCard()?.let { card ->
                    player.find { it.uid == t.askingPlayer }?.deck?.add(card)
                    if (player.find { it.uid == t.askingPlayer }?.let { checkForBooks(it) }!!) {
                        // Sets next player
                        _playerToTakeTurn.postValue(player[(indexOf(t.askingPlayer) +1) % player.size])
                    }else{
                        _playerToTakeTurn.postValue(player[indexOf(t.askingPlayer)])
                    }
                }?:_playerToTakeTurn.postValue(player[(indexOf(t.askingPlayer) +1) % player.size])
            }
            // Check for books in the current player's hand
            _gamePlayers.postValue(player)
            _hasEnded.value = checkEndGame()
        }
    }


    private fun checkForBooks(hand: Player): Boolean {
        hand.deck.groupBy { it.rank }.let { card ->
            for (rank in card.keys) {
                //Checks if all of current rank suit are in a players hand
                if (card[rank]?.size == 4) {
                    Log.d("CheckBook", "Player ${_playerToTakeTurn.value} books $rank")
                    hand.deck.removeAll { it.rank == rank }
                    hand.score += 1
                    checkForEmptyHand(hand)
                    return false
                }
            }
        }
        return true
    }

    private fun checkForEmptyHand(hand: Player) {
        if (hand.deck.isEmpty()) {
            deck.drawCard()?.let { hand.deck.add(it) }
        }
    }

    //Checks if game has ended
    override fun checkEndGame() =
        (deck.isEmpty() && gamePlayers.value?.all { it.deck.isEmpty() } ?: true)

    //Gets index of player with this uid
    private fun indexOf(uid: String) = gamePlayers.value?.indexOfFirst { it.uid == uid } ?: 0
}
