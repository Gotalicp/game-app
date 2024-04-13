package com.example.game_app.domain.game

import android.util.Log
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
        for (suit in Suit.values())
            for (rank in Rank.values())
                cards.add(Card(suit, rank))
    }

    fun deal(
        ids: List<String>,
        numberOfCardsPerPlayer: Int,
        deck: Deck
    ): Map<String, MutableList<Card>> = ids.associateWith {
        val playerCards = mutableListOf<Card>()
        repeat(numberOfCardsPerPlayer) {
            playerCards.add(deck.drawCard() ?: throw IllegalStateException("Deck is empty"))
        }
        playerCards
    }

    fun shuffle(seed: Long) {
        cards.shuffle(Random(seed))
    }

    fun showDeck() {
        for (card in cards)
            Log.d("ShowDeck", "${card.rank} of ${card.suit}")
    }

    fun drawCard() = if (cards.isNotEmpty()) cards.removeAt(0) else null

    fun isEmpty() = cards.isEmpty()

    fun deckSize() = cards.size
}