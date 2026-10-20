package com.example.ileauxtresors.bots

import com.example.ileauxtresors.model.ButinCard
import com.example.ileauxtresors.model.ButinEffect
import com.example.ileauxtresors.model.DieFace
import com.example.ileauxtresors.model.GameEngine
import com.example.ileauxtresors.model.GameRules
import com.example.ileauxtresors.model.GameState
import kotlin.random.Random

/** Joue le tour d'un bot. Les bots jouent sur PLATEAU entre deux tours du joueur. */
object BotPlayer {

    /** Coque en dessous de laquelle un bot au large n'attaque plus l'île. */
    private const val SAFE_HULL_TO_ATTACK = 5

    fun playTurn(state: GameState, random: Random = Random.Default): GameState {
        var s = GameEngine.rollDice(state, random)
        while (s.rollsLeft > 0) {
            val keep = chooseKeptDice(s)
            // Tous les dés sont utiles : inutile de relancer.
            if (keep.size == s.dice.size) break
            s = GameEngine.rollDice(s.copy(keptDice = keep), random)
        }
        s = GameEngine.resolveDice(s)
        return shop(s)
    }

    /**
     * Choisit les dés à garder selon la situation du bot :
     * - les pièces dont il a au moins une paire (une série de 3 rapporte de l'or) ;
     * - les charpentiers s'il est au large et abîmé (ils ne réparent pas sur l'île) ;
     * - les canons s'il tient l'île (ils touchent tout le monde) ou s'il peut en
     *   déloger le capitaine sans trop de risque ;
     * - les gourdes, pour acheter à la Taverne.
     */
    fun chooseKeptDice(state: GameState): Set<Int> {
        val me = state.current
        val onIsland = state.islandHolder == state.currentIndex
        val counts = state.dice.groupingBy { it }.eachCount()
        val missingHull = GameRules.MAX_HULL - me.hull

        val keepCannons = onIsland || (state.islandHolder != null && me.hull >= SAFE_HULL_TO_ATTACK)
        var carpentersToKeep = if (onIsland) 0 else missingHull

        return state.dice.indices.filterTo(mutableSetOf()) { i ->
            when (val face = state.dice[i]) {
                DieFace.CUIVRE, DieFace.ARGENT, DieFace.OR -> (counts[face] ?: 0) >= 2
                DieFace.CANON -> keepCannons
                DieFace.CHARPENTIER -> carpentersToKeep-- > 0
                DieFace.GOURDE -> true
            }
        }
    }

    /**
     * Le bot [index], touché sur l'île, décide s'il fuit.
     * Il reste s'il gagne au début de son prochain tour, sinon il fuit quand sa coque
     * ne tiendrait plus une salve des autres navires encore à flot.
     */
    fun shouldFlee(state: GameState, index: Int): Boolean {
        val bot = state.captains[index]
        if (bot.gold + GameRules.ISLAND_START_BONUS >= GameRules.GOLD_TO_WIN && bot.hull > 2) return false
        val opponentsAfloat = state.captains.withIndex().count { (i, c) -> i != index && !c.isSunk }
        return bot.hull <= 2 * opponentsAfloat
    }

    /** Achète la carte la plus utile qu'il peut payer, tant qu'une carte lui sert. */
    private fun shop(state: GameState): GameState {
        var s = state
        while (true) {
            val best = s.tavernCards.indices
                .filter { GameEngine.canBuy(s, it) && cardValue(s, s.tavernCards[it]) > 0 }
                .maxByOrNull { cardValue(s, s.tavernCards[it]) } ?: return s
            s = GameEngine.buyCard(s, best)
        }
    }

    /** Ce que la carte rapporte vraiment au bot courant, en « points » comparables. */
    fun cardValue(state: GameState, card: ButinCard): Int {
        val me = state.currentIndex
        val others = state.captains.filterIndexed { i, c -> i != me && !c.isSunk }
        return when (val effect = card.effect) {
            // L'or fait gagner : il compte double.
            is ButinEffect.GainGold -> 2 * effect.amount
            // Réparer une coque pleine ne sert à rien.
            is ButinEffect.Repair -> minOf(effect.amount, GameRules.MAX_HULL - state.current.hull)
            is ButinEffect.DamageOthers -> effect.amount * others.size
            is ButinEffect.Plunder -> 2 * others.sumOf { minOf(effect.amount, it.gold) }
        }
    }
}
