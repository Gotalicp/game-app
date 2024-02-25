package com.example.game_app.domain.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.game_app.domain.LobbyProvider
import com.example.game_app.domain.SharedInformation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
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
        var uid: String,
        var score: Int
    ) : Serializable

    //All the players
    private val _gamePlayers = MutableLiveData<MutableList<Player>>()
    val gamePlayers: LiveData<MutableList<Player>> get() = _gamePlayers

    //Checking who's turn is it
    private val _playerToTakeTurn = MutableSharedFlow<String>()
    val playerToTakeTurn: Flow<String?> get() = _playerToTakeTurn

    private val _hasEnded = MutableStateFlow(false)
    val hasEnded: Flow<Boolean> get() = _hasEnded

    private val _seed = MutableStateFlow<Long?>(null)
    val seed: Flow<Long?> get() = _seed

    private val _play = MutableLiveData<Pair<Play, Int>>()
    val play: LiveData <Pair<Play, Int>> get() = _play

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

    override suspend fun startGame(seed: Long) {
        if (_gamePlayers.value == null) {
            LobbyProvider.getLobby().value?.players?.let { setPlayer(it) }
        }
        _hasEnded.value = false
        deck = resetDeck()
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
                    _playerToTakeTurn.emit(players[0].uid)
                }
            }
        }
    }

    override suspend fun turnHandling(t: Play) {
        // Check if the asked player has any cards of the requested rank
        val askingIndex = indexOf(t.askingPlayer)
        _gamePlayers.value?.let { player ->
            val cardsReceived = player[indexOf(t.askedPlayer)].deck.filter { it.rank == t.rank }
            //Add whats happening to the game
                _play.postValue(Pair(t, cardsReceived.size))
            //If there are card give them
            if (cardsReceived.isNotEmpty()) {
                player.find { it.uid == t.askingPlayer }?.let {
                    it.deck.addAll(cardsReceived)
                    hasBook(it, askingIndex)
                }
                player.find { it.uid == t.askedPlayer }?.let {
                    it.deck.removeAll(cardsReceived)
                    if (isHandEmpty(it)) {
                        drawCard(it)
                    }
                }
                setSamePlayer(indexOf(t.askingPlayer))
            } else {
                // If no cards received, draw a card from the deck
                player.find { it.uid == t.askingPlayer }?.let {
                    drawCard(it)
                    if (hasBook(it, askingIndex)) {
                        setSamePlayer(askingIndex)
                    } else {
                        nextPlayer(askingIndex)
                    }
                } ?: nextPlayer(askingIndex)
            }
            // Check for books in the current player's hand
            _gamePlayers.postValue(player)
        }
    }


    private suspend fun hasBook(hand: Player, index: Int): Boolean {
        hand.deck.groupBy { it.rank }.let { card ->
            for (rank in card.keys) {
                //Checks if all of current rank suit are in a players hand
                if (card[rank]?.size == 4) {
                    Log.d("CheckBook", "Player $_playerToTakeTurn books $rank")
                    hand.deck.removeAll { it.rank == rank }
                    hand.score += 1
                    if (isHandEmpty(hand)) {
                        drawCard(hand) ?: nextPlayer(index)
                    }
                    return true
                }
            }
        }
        return false
    }

    private fun isHandEmpty(hand: Player) = hand.deck.isEmpty()
    private fun drawCard(hand: Player) = deck.drawCard()?.let { hand.deck.add(it) }

    //Checks if game has ended
    override fun checkEndGame() =
        (deck.isEmpty() && gamePlayers.value?.all { it.deck.isEmpty() } ?: true)

    //Gets index of player with this uid
    private fun indexOf(uid: String) = gamePlayers.value?.indexOfFirst { it.uid == uid } ?: 0

    //Sets the next player
    private suspend fun nextPlayer(index: Int) {
        checkEndGame().let { ended ->
            if (!ended) {
                gamePlayers.value?.let {
                    if (it[(index + 1) % it.size].deck.isNotEmpty()) {
                        _playerToTakeTurn.emit(it[(index + 1) % it.size].uid)
                    } else {
                        nextPlayer(index + 1)
                    }
                }
            } else {
                _hasEnded.value = true
            }
        }
    }

    private suspend fun setSamePlayer(index: Int) {
        if (!_hasEnded.value) {
            gamePlayers.value?.get(index)?.let {
                if (it.deck.isNotEmpty()) {
                    _playerToTakeTurn.emit(it.uid)
                } else {
                    nextPlayer(index)
                }
            }
        }
    }

    suspend fun skipPlayer(uid: String) {
        nextPlayer(indexOf(uid))
    }

    fun getPlayer(uid: String) = _gamePlayers.value?.find { it.uid == uid }
}
