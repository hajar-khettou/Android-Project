package com.example.ileauxtresors.bots

import com.example.ileauxtresors.model.GameEngine
import com.example.ileauxtresors.model.GameRules
import com.example.ileauxtresors.model.GameState

/** Joue le tour d'un bot. Les bots jouent sur PLATEAU entre deux tours du joueur. */
object BotPlayer {

    fun playTurn(state: GameState): GameState {
        // TODO : choisir quels dés garder et relancer.
        var s = GameEngine.rollDice(state)
        s = GameEngine.resolveDice(s)
        return shop(s)
    }

    /** Le bot [index], touché sur l'île, décide s'il fuit. */
    fun shouldFlee(state: GameState, index: Int): Boolean {
        // TODO : stratégie à affiner (or de l'attaquant, nombre de navires restants…).
        return state.captains[index].hull <= GameRules.MAX_HULL / 2
    }

    /** Achète la carte la plus chère qu'il peut payer, tant qu'il en reste une. */
    private fun shop(state: GameState): GameState {
        var s = state
        while (true) {
            val best = s.tavernCards.indices
                .filter { GameEngine.canBuy(s, it) }
                .maxByOrNull { s.tavernCards[it].cost } ?: return s
            s = GameEngine.buyCard(s, best)
        }
    }
}
