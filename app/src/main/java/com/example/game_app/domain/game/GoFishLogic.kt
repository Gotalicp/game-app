package com.example.game_app.domain.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
        var player: PlayerWrapper
    ) : Serializable

    //All the players
    private val _gamePlayers = MutableLiveData<MutableList<Player>>()
    val gamePlayers: LiveData<MutableList<Player>> get() = _gamePlayers

    //Checking who's turn is it
    private val _playerToTakeTurn = MutableSharedFlow<String>()
    override val playerToTakeTurn: Flow<String?> get() = _playerToTakeTurn

    private val _hasEnded = MutableStateFlow(false)
    override val hasEnded: Flow<Boolean> get() = _hasEnded

    private val _seed = MutableStateFlow<Long?>(null)
    override val seed: Flow<Long?> get() = _seed

    private val _play = MutableLiveData<Pair<Play, Int>>()
    val play: LiveData<Pair<Play, Int>> get() = _play

    //Initiate deck
    private var deck = Deck()
    private fun resetDeck() = Deck()
    fun getDeckSize() = deck.deckSize()

    fun getPlayer(uid: String) = _gamePlayers.value?.find { it.player.uid == uid }

    override fun setPlayer(players: MutableList<String>) {
        _gamePlayers.value =
            players.map { Player(mutableListOf(), PlayerWrapper(it, 0)) }.toMutableList()
    }

    override fun updateSeed(seed: Long) {
        _seed.value = seed
    }

    override suspend fun startGame(seed: Long) {
        _hasEnded.value = false
        deck = resetDeck()
        deck.shuffle(seed)
        if (!_gamePlayers.value.isNullOrEmpty()) {
            _gamePlayers.value?.let { players ->
                players.shuffle(Random(seed))
                deck.deal(players.map { it.player.uid }, 5, deck).let {
                    players.forEach { player ->
                        it[player.player.uid]?.let {
                            player.deck.addAll(it)
                        }
                    }
                    _gamePlayers.postValue(players)
                    _playerToTakeTurn.emit(players[0].player.uid)
                }
            }
        }
    }

    override suspend fun turnHandling(t: Play) {
        // Check if the asked player has any cards of the requested rank
        val askingIndex = indexOf(t.askingPlayer)
        val askedIndex = indexOf(t.askedPlayer)
        _gamePlayers.value?.let { player ->
            val cardsReceived = player[askedIndex].deck.filter { it.rank == t.rank }
            _play.postValue(Pair(t, cardsReceived.size))
            if (cardsReceived.isNotEmpty()) {
                player[askingIndex].deck.addAll(cardsReceived)
                hasBook(askingIndex)
                player[askedIndex].deck.removeAll(cardsReceived)
                if (isHandEmpty(askingIndex) == true) {
                    drawCard(askingIndex)
                }
            } else {
                drawCard(askingIndex)
                hasBook(askingIndex)
            }
            _gamePlayers.postValue(player)
        }
    }

    private suspend fun hasBook(index: Int) {
        _gamePlayers.value?.get(index)?.let { player ->
            player.deck.groupBy { it.rank }.let { card ->
                for (rank in card.keys) {
                    if (card[rank]?.size == 4) {
                        Log.d("CheckBook", "Player $_playerToTakeTurn books $rank")
                        player.deck.removeAll { it.rank == rank }
                        player.player.score.plus(1)
                        if (isHandEmpty(index) == true) {
                            drawCard(index) ?: nextPlayer(index)
                        }
                        setSamePlayer(index)
                    }
                }
            }
            nextPlayer(index)
        }
    }

    private fun isHandEmpty(index: Int) = _gamePlayers.value?.get(index)?.deck?.isEmpty()
    private fun drawCard(index: Int) =
        deck.drawCard()?.let { _gamePlayers.value?.get(index)?.deck?.add(it) }

    //Checks if game has ended
    override fun checkEndGame() =
        (deck.isEmpty() && gamePlayers.value?.all { it.deck.isEmpty() } ?: true)

    //Gets index of player with this uid
    private fun indexOf(uid: String) = gamePlayers.value?.indexOfFirst { it.player.uid == uid } ?: 0

    //Sets the next player
    private suspend fun nextPlayer(index: Int) {
        checkEndGame().let { ended ->
            if (!ended) {
                gamePlayers.value?.let {
                    if (it[(index + 1) % it.size].deck.isNotEmpty()) {
                        _playerToTakeTurn.emit(it[(index + 1) % it.size].player.uid)
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
                    _playerToTakeTurn.emit(it.player.uid)
                } else {
                    nextPlayer(index)
                }
            }
        }
    }

    suspend fun skipPlayer(uid: String) {
        nextPlayer(indexOf(uid))
    }
}
