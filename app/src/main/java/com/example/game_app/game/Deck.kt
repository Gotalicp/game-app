package com.example.game_app.game

import android.util.Log
import com.example.game_app.data.PlayerInfo
import com.example.game_app.game.goFish.GoFishLogic
import kotlin.random.Random

enum class Suit {
    HEARTS,
    DIAMONDS,
    CLUBS,
    SPADES
}

enum class Rank(val value: Int) {
    ACE(1), TWO(2), THREE(3), FOUR(4), FIVE(5),
    SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10),
    JACK(11), QUEEN(12), KING(13)
}

data class Card(val suit: Suit, val rank: Rank)

class Deck{
    private val cards: MutableList<Card> = mutableListOf()
    init {
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                cards.add(Card(suit, rank))
            }
        }
    }

    //Gives Each Player (N) Number Of Cards
    fun deal(players: MutableList<PlayerInfo>, numberOfCardsPerPlayer: Int, deck: Deck): MutableList<GoFishLogic.Player> {
        //create empty temporary list to return later
        val playersHands = mutableListOf<GoFishLogic.Player>()
        for(player in players){
            //create hand for every player
            playersHands.add(GoFishLogic.Player(mutableListOf(),player,0))
        }
        //Gives Card To Players
        for (i in 1..numberOfCardsPerPlayer) {
            for(player in playersHands) {
                player.deck.add(deck.drawCard() ?: throw IllegalStateException("Deck is empty"))
            }
        }
        return playersHands
    }

    //Gives players all the cards
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
            Log.d("ShowDeck","${card.rank} of ${card.suit}")
        }
    }
    fun drawCard() = if (cards.isNotEmpty()) { cards.removeAt(0) } else { null }
    fun isEmpty() = cards.isEmpty()
}