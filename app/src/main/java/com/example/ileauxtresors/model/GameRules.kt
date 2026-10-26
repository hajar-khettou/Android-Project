package com.example.ileauxtresors.model

/** Les valeurs du jeu, regroupées ici pour pouvoir les ajuster facilement. */
object GameRules {
    const val PLAYER_COUNT = 4
    const val GOLD_TO_WIN = 20
    const val DICE_COUNT = 6
    /** 1 lancer + 2 relances. */
    const val ROLLS_PER_TURN = 3
    /** Commencer son tour sur l'île. */
    const val ISLAND_START_BONUS = 2
    /** S'amarrer à l'île quand elle est libre. */
    const val ISLAND_DOCK_BONUS = 1
    const val TAVERN_VISIBLE_CARDS = 3
    /** Prix en gourdes pour remplacer les 3 cartes visibles. */
    const val SWEEP_COST = 2

    /** Points de coque au départ, et maximum (comme les 10 PV de King of Tokyo). */
    const val MAX_HULL = 10
}
