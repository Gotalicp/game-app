package com.example.game_app.domain.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.game_app.domain.SharedInformation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.Serializable
import java.util.Random

class GoFishLogic : GameLogic<GoFishLogic.Play> {
    data class Play(
        val askingPlayer: String,
        val askedPlayer: String,
        val rank: Rank
    ) : Serializable

    data class Player(
        val deck: MutableList<Card>,
        val uid: String,
        var score: Int
    ) : Serializable

    //All the players
    private val _gamePlayers = MutableLiveData<MutableList<Player>>()
    val gamePlayers: LiveData<MutableList<Player>> get() = _gamePlayers

    //Checking who's turn is it
    private val _playerToTakeTurn = MutableStateFlow<String?>(null)
    val playerToTakeTurn: Flow<String?> get() = _playerToTakeTurn

    private val _hasEnded = MutableStateFlow(false)
    val hasEnded: Flow<Boolean> get() = _hasEnded

    private val _seed = MutableStateFlow<Long?>(null)
    val seed: Flow<Long?> get() = _seed

    private val _play = MutableLiveData<MutableList<Pair<Play, Int>>>()
    val play: LiveData<MutableList<Pair<Play, Int>>> get() = _play

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
        _play.postValue(mutableListOf())
        deck.shuffle(seed)
        deck.showDeck()
        if (!_gamePlayers.value.isNullOrEmpty()) {
            _gamePlayers.value?.let { players ->
                players.shuffle(Random(seed))
                deck.deal(players.map { it.uid }, 5, deck).let {
                    players.forEach { player ->
                        it[player.uid]?.let {
                            player.deck.addAll(it)
                        }
                    }
                    _gamePlayers.postValue(players)
                    _playerToTakeTurn.value = players[0].uid
                }
            }
        }
    }

    override fun turnHandling(t: Play) {
        // Check if the asked player has any cards of the requested rank
        _gamePlayers.value?.let { player ->
            val cardsReceived = player[indexOf(t.askedPlayer)].deck.filter { it.rank == t.rank }
            if (cardsReceived.isNotEmpty()) {
                player.find { it.uid == t.askingPlayer }?.deck?.addAll(cardsReceived)
                _play.value?.let { play ->
                    play.add(Pair(t, cardsReceived.size))
                    _play.postValue(play)
                }
                player.find { it.uid == t.askedPlayer }?.deck?.removeAll(cardsReceived)
                player.find { it.uid == t.askedPlayer }?.let {
                    if (isHandEmpty(it)) {
                        drawCard(it)
                    }
                }
                player.find { it.uid == t.askingPlayer }
                    ?.let { checkForBooks(it, indexOf(t.askingPlayer)) }
            } else {
                // If no cards received, draw a card from the deck
                deck.drawCard()?.let { card ->
                    _play.value?.let { play ->
                        play.add(Pair(t, cardsReceived.size))
                        _play.postValue(play)
                    }
                    player.find { it.uid == t.askingPlayer }?.deck?.add(card)
                    if (player.find { it.uid == t.askingPlayer }
                            ?.let { checkForBooks(it, indexOf(t.askingPlayer)) }!!) {
                        nextPlayer(indexOf(t.askingPlayer))
                    }
                } ?: nextPlayer(indexOf(t.askingPlayer))
            }
            // Check for books in the current player's hand
            _gamePlayers.postValue(player)
            _hasEnded.value = checkEndGame()
        }
    }


    private fun checkForBooks(hand: Player, index: Int): Boolean {
        hand.deck.groupBy { it.rank }.let { card ->
            for (rank in card.keys) {
                //Checks if all of current rank suit are in a players hand
                if (card[rank]?.size == 4) {
                    Log.d("CheckBook", "Player ${_playerToTakeTurn.value} books $rank")
                    hand.deck.removeAll { it.rank == rank }
                    hand.score += 1
                    if (isHandEmpty(hand)) {
                        drawCard(hand) ?: nextPlayer(index)
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun isHandEmpty(hand: Player) = hand.deck.isEmpty()
    private fun drawCard(hand: Player) = deck.drawCard()?.let { hand.deck.add(it) }

    //Checks if game has ended
    override fun checkEndGame() =
        (deck.isEmpty() && gamePlayers.value?.all { it.deck.isEmpty() } ?: true)

    //Gets index of player with this uid
    private fun indexOf(uid: String) = gamePlayers.value?.indexOfFirst { it.uid == uid } ?: 0

    //Sets the next player
    private fun nextPlayer(index: Int) {
        if (!_hasEnded.value) {
            gamePlayers.value?.let {
                if (it[(index + 1) % it.size].deck.isNotEmpty()) {
                    _playerToTakeTurn.value = it[(index + 1) % it.size].uid
                } else {
                    nextPlayer(index + 1)
                }
            }
        }
    }
    fun skipPlayer(){
        _playerToTakeTurn.value?.let { indexOf(it) }?.let { nextPlayer(it) }
    }
}
