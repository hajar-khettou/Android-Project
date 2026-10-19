package com.example.ileauxtresors.model

import kotlin.random.Random

/** Le capitaine sur l'île vient d'être touché : il doit choisir de rester ou de fuir. */
data class IslandHit(val attackerIndex: Int, val damage: Int)

/** Tout l'état d'une partie. Immuable : chaque action renvoie un nouveau GameState. */
data class GameState(
    val captains: List<Captain>,
    /** Index dans [captains] du joueur dont c'est le tour. */
    val currentIndex: Int = 0,
    /** Index du capitaine sur l'île, ou null si elle est libre. */
    val islandHolder: Int? = null,
    /** Non null tant que le capitaine sur l'île n'a pas répondu à une attaque. */
    val islandHit: IslandHit? = null,
    val dice: List<DieFace> = emptyList(),
    /** Index des dés gardés (« GARDÉ ») qui ne seront pas relancés. */
    val keptDice: Set<Int> = emptySet(),
    val rollsLeft: Int = GameRules.ROLLS_PER_TURN,
    /** Les cartes visibles à la Taverne. */
    val tavernCards: List<ButinCard> = emptyList(),
    /** La pioche, face cachée. */
    val deck: List<ButinCard> = emptyList(),
    val turn: Int = 1,
) {
    /** Le joueur humain est toujours le premier de la liste. */
    val player: Captain get() = captains[PLAYER_INDEX]
    val bots: List<Captain> get() = captains.drop(1)
    val current: Captain get() = captains[currentIndex]

    companion object {
        const val PLAYER_INDEX = 0

        /** Le joueur prend le navire choisi, les 3 bots prennent les autres. La Taverne montre 3 cartes. */
        fun newGame(playerName: String, playerShip: Ship, random: Random = Random.Default): GameState {
            val player = Captain(name = playerName, ship = playerShip, isBot = false)
            val bots = Ship.entries
                .filter { it != playerShip }
                .map { Captain(name = it.displayName, ship = it, isBot = true) }
            val deck = ButinDeck.cards.shuffled(random)
            return GameState(
                captains = listOf(player) + bots,
                tavernCards = deck.take(GameRules.TAVERN_VISIBLE_CARDS),
                deck = deck.drop(GameRules.TAVERN_VISIBLE_CARDS),
            )
        }
    }
}
