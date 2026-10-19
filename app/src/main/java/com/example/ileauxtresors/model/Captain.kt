package com.example.ileauxtresors.model

/** Un joueur : le capitaine humain ou l'un des 3 bots. */
data class Captain(
    val name: String,
    val ship: Ship,
    val isBot: Boolean,
    val gold: Int = 0,
    val hull: Int = GameRules.MAX_HULL,
    val gourds: Int = 0,
    val cards: List<ButinCard> = emptyList(),
) {
    val isSunk: Boolean get() = hull <= 0

    fun damaged(damage: Int): Captain = copy(hull = maxOf(0, hull - damage))
}
