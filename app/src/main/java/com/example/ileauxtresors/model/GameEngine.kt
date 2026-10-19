package com.example.ileauxtresors.model

import kotlin.random.Random

/**
 * Les règles du jeu, sans aucune dépendance Android pour pouvoir les tester
 * en tests unitaires. Chaque fonction prend un état et renvoie le nouvel état.
 */
object GameEngine {

    /** Lance les dés non gardés. Le premier lancer du tour lance les 6 dés. */
    fun rollDice(state: GameState, random: Random = Random.Default): GameState {
        if (state.rollsLeft <= 0) return state
        val dice = List(GameRules.DICE_COUNT) { i ->
            if (i in state.keptDice && i < state.dice.size) state.dice[i]
            else DieFace.entries.random(random)
        }
        return state.copy(dice = dice, rollsLeft = state.rollsLeft - 1)
    }

    /** Garde ou libère un dé entre deux lancers. */
    fun toggleKeep(state: GameState, index: Int): GameState {
        val kept = if (index in state.keptDice) state.keptDice - index else state.keptDice + index
        return state.copy(keptDice = kept)
    }

    /** Début du tour : +2 or si le joueur courant commence sur l'île. */
    fun startTurn(state: GameState): GameState {
        // TODO : appliquer ISLAND_START_BONUS.
        return state.copy(dice = emptyList(), keptDice = emptySet(), rollsLeft = GameRules.ROLLS_PER_TURN)
    }

    /** Applique les dés validés : or, canons, charpentiers, gourdes, puis amarrage à l'île. */
    fun resolveDice(state: GameState): GameState {
        // TODO : séries de pièces, dégâts des canons, réparations (pas sur l'île), gourdes,
        //  amarrage si l'île est libre (+ISLAND_DOCK_BONUS).
        return state
    }

    /** Le capitaine sur l'île choisit de fuir : l'attaquant prend sa place. */
    fun fleeIsland(state: GameState, attackerIndex: Int): GameState {
        // TODO : à vérifier quand resolveDice sera écrit.
        return state.copy(islandHolder = attackerIndex)
    }

    /** Passe au joueur suivant encore à flot. */
    fun nextTurn(state: GameState): GameState {
        val count = state.captains.size
        var next = (state.currentIndex + 1) % count
        while (state.captains[next].isSunk && next != state.currentIndex) {
            next = (next + 1) % count
        }
        val turn = if (next <= state.currentIndex) state.turn + 1 else state.turn
        return startTurn(state.copy(currentIndex = next, turn = turn))
    }

    /** Le vainqueur : 20 pièces d'or, ou dernier navire à flot. Null si la partie continue. */
    fun winner(state: GameState): Captain? {
        state.captains.firstOrNull { !it.isSunk && it.gold >= GameRules.GOLD_TO_WIN }?.let { return it }
        val afloat = state.captains.filter { !it.isSunk }
        return afloat.singleOrNull()
    }
}
