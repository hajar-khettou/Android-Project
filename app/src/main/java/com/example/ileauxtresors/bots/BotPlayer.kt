package com.example.ileauxtresors.bots

import com.example.ileauxtresors.model.GameEngine
import com.example.ileauxtresors.model.GameState

/** Joue le tour d'un bot. Les bots jouent sur PLATEAU entre deux tours du joueur. */
object BotPlayer {

    fun playTurn(state: GameState): GameState {
        // TODO : choisir quels dés garder, quand fuir l'île, quelles cartes acheter.
        var s = GameEngine.rollDice(state)
        s = GameEngine.resolveDice(s)
        return s
    }
}
