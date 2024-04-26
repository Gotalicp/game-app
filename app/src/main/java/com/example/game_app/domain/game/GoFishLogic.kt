package com.example.game_app.domain.game

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

    private val _gamePlayers = MutableLiveData<MutableList<Player>>()
    val gamePlayers: LiveData<MutableList<Player>> get() = _gamePlayers

    private var playerTurn: String = ""
    private val _playerToTakeTurn = MutableSharedFlow<String>()
    override val playerToTakeTurn: Flow<String?> get() = _playerToTakeTurn

    private val _hasEnded = MutableStateFlow(false)
    override val hasEnded: Flow<Boolean> get() = _hasEnded

    private val _seed = MutableStateFlow<Long?>(null)
    override val seed: Flow<Long?> get() = _seed

    private val _play = MutableLiveData<Pair<Play, Int>>()
    val play: LiveData<Pair<Play, Int>> get() = _play

    private var deck = Deck()

    fun getDeckSize() = deck.deckSize()

    fun getPlayer(uid: String) = _gamePlayers.value?.find { it.player.uid == uid }

    override fun setPlayer(players: List<String>) {
        _gamePlayers.postValue(players.map { Player(mutableListOf(), PlayerWrapper(it, 0)) }
            .toMutableList())
    }

    override fun updateSeed(seed: Long) {
        _seed.value = seed
    }

    override suspend fun startGame(seed: Long) {
        _hasEnded.value = false
        deck = Deck()
        deck.shuffle(seed)
        _gamePlayers.value?.let { players ->
            players.shuffle(Random(seed))
            deck.deal(players.map { it.player.uid }, 5, deck).let {
                players.forEach { player ->
                    it[player.player.uid]?.let {
                        player.deck.addAll(it)
                    }
                }
            }
            _gamePlayers.postValue(players)
            keepTrackOfCringe(players[0].player.uid)
        }
    }

    override suspend fun turnHandling(t: Play) {
        if (playerTurn == t.askingPlayer) {
            val askingIndex = indexOf(t.askingPlayer)
            val askedIndex = indexOf(t.askedPlayer)
            _gamePlayers.value?.let { player ->
                val cardsReceived = player[askedIndex].deck.filter { it.rank == t.rank }
                _play.postValue(Pair(t, cardsReceived.size))
                if (cardsReceived.isNotEmpty()) {
                    player[askingIndex].deck.addAll(cardsReceived)
                    player[askedIndex].deck.removeAll(cardsReceived)
                    if (isHandEmpty(askedIndex)) {
                        drawCard(askedIndex)
                    }
                    if (hasBook(askingIndex) == null) {
                        nextPlayer(askingIndex)
                    } else {
                        setSamePlayer(askingIndex)
                    }
                } else {
                    drawCard(askingIndex)
                    if (hasBook(askingIndex) == true) {
                        setSamePlayer(askingIndex)
                    } else {
                        nextPlayer(askingIndex)
                    }
                }
                _gamePlayers.postValue(player)
            }
        }
    }

    private fun hasBook(index: Int): Boolean? {
        _gamePlayers.value?.get(index)?.let { player ->
            player.deck.groupBy { it.rank }.let { card ->
                for (rank in card.keys) {
                    if (card[rank]?.size == 4) {
                        player.deck.removeAll { it.rank == rank }
                        player.player.score = player.player.score.plus(1)
                        _hasEnded.value = checkEndGame()
                        if (isHandEmpty(index)) {
                            drawCard(index) ?: return null
                            hasBook(index)
                            return true
                        } else {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    private fun isHandEmpty(index: Int) = _gamePlayers.value?.get(index)?.deck?.isEmpty() ?: true

    private fun drawCard(index: Int) =
        deck.drawCard()?.let { _gamePlayers.value?.get(index)?.deck?.add(it) }

    override fun checkEndGame() =
        (deck.isEmpty() && _gamePlayers.value?.all { it.deck.isEmpty() } ?: true)

    private fun indexOf(uid: String) =
        _gamePlayers.value?.indexOfFirst { it.player.uid == uid } ?: 0

    private suspend fun nextPlayer(index: Int) {
        if (!_hasEnded.value) {
            _gamePlayers.value?.let {
                if (it[(index + 1) % it.size].deck.isNotEmpty()) {
                    keepTrackOfCringe(it[(index + 1) % it.size].player.uid)
                } else {
                    nextPlayer(index + 1)
                }
            }
        }
    }

    private suspend fun setSamePlayer(index: Int) {
        if (!_hasEnded.value) {
            _gamePlayers.value?.get(index)?.let {
                if (it.deck.isNotEmpty()) {
                    keepTrackOfCringe(it.player.uid)
                } else {
                    nextPlayer(index)
                }
            }
        }
    }

    suspend fun skipPlayer(uid: String) {
        nextPlayer(indexOf(uid))
    }

    private suspend fun keepTrackOfCringe(next: String) {
        playerTurn = next
        _playerToTakeTurn.emit(next)
    }
}
