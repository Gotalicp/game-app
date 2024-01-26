package com.example.game_app.domain

import android.util.Log
import com.example.game_app.ui.game.goFish.GoFishLogic
import kotlin.random.Random

enum class Suit {
    HEART,
    DIAMOND,
    CLUB,
    SPADES
}

enum class Rank(val value: Int) {
    ACE(1), TWO(2), THREE(3), FOUR(4), FIVE(5),
    SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10),
    JACK(11), QUEEN(12), KING(13)
}

data class Card(val suit: Suit, val rank: Rank)

class Deck {
    private val cards: MutableList<Card> = mutableListOf()

    init {
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                cards.add(Card(suit, rank))
            }
        }
    }

    fun deal(players: MutableList<GoFishLogic.Player>, numberOfCardsPerPlayer: Int, deck: Deck) =
        players.apply {
            for (i in 1..numberOfCardsPerPlayer) {
                for (player in players) {
                    player.deck.add(deck.drawCard() ?: throw IllegalStateException("Deck is empty"))
                }
            }
        }

    fun deal(players: List<Int>, deck: Deck): MutableList<Pair<Int, MutableList<Card>>> {
        val playerHands = mutableMapOf<Int, MutableList<Card>>()
        // Initialize hands for each player
        for (player in players) {
            playerHands[player] = mutableListOf()
        }
        //Loop of giving cards util empty
        while (deck.isEmpty()) {
            for (player in players) {
                val card = deck.drawCard() ?: break
                playerHands[player]?.add(card)
            }
        }
        // Convert playerHands to a list of pairs
        return playerHands.toList().toMutableList()
    }

    fun shuffle(seed: Long) {
        cards.shuffle(Random(seed))
    }

    fun showDeck() {
        for (card in cards) {
            Log.d("ShowDeck", "${card.rank} of ${card.suit}")
        }
    }

    fun drawCard() = if (cards.isNotEmpty()) {
        cards.removeAt(0)
    } else {
        null
    }

    fun isEmpty() = cards.isEmpty()

    fun deckSize() = cards.size
}