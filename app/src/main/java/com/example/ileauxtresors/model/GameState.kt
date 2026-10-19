package com.example.ileauxtresors.model

/** Tout l'état d'une partie. Immuable : chaque action renvoie un nouveau GameState. */
data class GameState(
    val captains: List<Captain>,
    /** Index dans [captains] du joueur dont c'est le tour. */
    val currentIndex: Int = 0,
    /** Index du capitaine sur l'île, ou null si elle est libre. */
    val islandHolder: Int? = null,
    val dice: List<DieFace> = emptyList(),
    /** Index des dés gardés (« GARDÉ ») qui ne seront pas relancés. */
    val keptDice: Set<Int> = emptySet(),
    val rollsLeft: Int = GameRules.ROLLS_PER_TURN,
    val tavernCards: List<ButinCard> = emptyList(),
    val turn: Int = 1,
) {
    /** Le joueur humain est toujours le premier de la liste. */
    val player: Captain get() = captains[PLAYER_INDEX]
    val bots: List<Captain> get() = captains.drop(1)
    val current: Captain get() = captains[currentIndex]

    companion object {
        const val PLAYER_INDEX = 0

        /** Le joueur prend le navire choisi, les 3 bots prennent les autres. */
        fun newGame(playerName: String, playerShip: Ship): GameState {
            val player = Captain(name = playerName, ship = playerShip, isBot = false)
            val bots = Ship.entries
                .filter { it != playerShip }
                .map { Captain(name = it.displayName, ship = it, isBot = true) }
            return GameState(captains = listOf(player) + bots)
        }
    }
}
