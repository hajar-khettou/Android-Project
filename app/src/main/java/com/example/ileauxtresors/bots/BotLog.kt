package com.example.ileauxtresors.bots

import com.example.ileauxtresors.model.DieFace
import com.example.ileauxtresors.model.GameRules
import com.example.ileauxtresors.model.GameState

/** Raconte en une ligne ce qu'un bot a fait pendant son tour, pour l'afficher au joueur. */
object BotLog {

    /**
     * Compare l'état au début du tour du bot ([before], après [com.example.ileauxtresors.model.GameEngine.startTurn])
     * et l'état à la fin de son tour ([after]).
     */
    fun describeTurn(before: GameState, after: GameState): String {
        val me = before.currentIndex
        val name = before.captains[me].name
        val parts = mutableListOf<String>()

        if (before.islandHolder == me) parts += "+${GameRules.ISLAND_START_BONUS} or sur l'île"

        val gold = after.captains[me].gold - before.captains[me].gold
        if (gold > 0) parts += "+$gold or"
        val repaired = after.captains[me].hull - before.captains[me].hull
        if (repaired > 0) parts += "répare $repaired coque"
        val gourds = after.dice.count { it == DieFace.GOURDE }
        if (gourds > 0) parts += "+$gourds gourde" + if (gourds > 1) "s" else ""

        before.captains.indices.filter { it != me }.forEach { i ->
            val loss = before.captains[i].hull - after.captains[i].hull
            val target = before.captains[i].name
            when {
                loss <= 0 -> Unit
                after.captains[i].isSunk -> parts += "coule $target"
                else -> parts += "touche $target (−$loss)"
            }
        }

        if (after.islandHolder == me && before.islandHolder != me) {
            val previous = before.islandHolder
            parts += if (previous == null) "s'amarre à l'île" else "chasse ${before.captains[previous].name} de l'île"
        }

        boughtCards(before, after).forEach { parts += "achète « $it »" }

        return "$name : " + if (parts.isEmpty()) "rien de notable" else parts.joinToString(", ")
    }

    /** Les bots ne balaient jamais : une carte qui a quitté la Taverne et la pioche a été achetée. */
    private fun boughtCards(before: GameState, after: GameState): List<String> {
        val left = (after.tavernCards + after.deck).map { it.name }.toMutableList()
        return (before.tavernCards + before.deck).map { it.name }.filter { !left.remove(it) }
    }
}
