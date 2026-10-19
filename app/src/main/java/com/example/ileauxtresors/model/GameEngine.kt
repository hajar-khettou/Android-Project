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

    /** Début du tour : remet les dés à zéro, +2 or si le joueur courant commence sur l'île. */
    fun startTurn(state: GameState): GameState {
        val reset = state.copy(dice = emptyList(), keptDice = emptySet(), rollsLeft = GameRules.ROLLS_PER_TURN)
        return if (state.islandHolder == state.currentIndex) {
            reset.updateCaptain(state.currentIndex) { it.copy(gold = it.gold + GameRules.ISLAND_START_BONUS) }
        } else {
            reset
        }
    }

    /**
     * Applique les dés validés du joueur courant : or, gourdes, charpentiers, canons,
     * puis amarrage à l'île si elle est libre.
     * Si le capitaine sur l'île est touché, [GameState.islandHit] est rempli et il faut
     * appeler [answerIslandAttack] avec sa décision.
     */
    fun resolveDice(state: GameState): GameState {
        val me = state.currentIndex
        val onIsland = state.islandHolder == me
        val counts = state.dice.groupingBy { it }.eachCount()
        fun count(face: DieFace) = counts[face] ?: 0

        var s = state.updateCaptain(me) { c ->
            c.copy(
                gold = c.gold + coinGold(state.dice),
                gourds = c.gourds + count(DieFace.GOURDE),
                hull = if (onIsland) c.hull else minOf(GameRules.MAX_HULL, c.hull + count(DieFace.CHARPENTIER)),
            )
        }
        val cannons = count(DieFace.CANON)
        if (cannons > 0) s = fireCannons(s, cannons)
        if (s.islandHolder == null && s.islandHit == null) s = dock(s, me)
        return s
    }

    /** Or rapporté par les dés : 3 faces identiques = leur valeur, +1 par face identique en plus. */
    fun coinGold(dice: List<DieFace>): Int =
        dice.filter { it.coinValue > 0 }
            .groupingBy { it }
            .eachCount()
            .entries
            .sumOf { (face, n) -> if (n >= 3) face.coinValue + (n - 3) else 0 }

    /** Réponse du capitaine sur l'île à l'attaque : s'il fuit, l'attaquant prend sa place. */
    fun answerIslandAttack(state: GameState, flee: Boolean): GameState {
        val hit = state.islandHit ?: return state
        val answered = state.copy(islandHit = null)
        return if (flee) dock(answered, hit.attackerIndex) else answered
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

    /** Depuis l'île, les canons touchent tous les autres ; au large, seulement celui sur l'île. */
    private fun fireCannons(state: GameState, damage: Int): GameState {
        val me = state.currentIndex
        val holder = state.islandHolder ?: return state
        if (holder == me) {
            return state.copy(captains = state.captains.mapIndexed { i, c ->
                if (i == me || c.isSunk) c else c.damaged(damage)
            })
        }
        val hit = state.updateCaptain(holder) { it.damaged(damage) }
        return if (hit.captains[holder].isSunk) hit.copy(islandHolder = null)
        else hit.copy(islandHit = IslandHit(attackerIndex = me, damage = damage))
    }

    /** Le capitaine [index] s'amarre à l'île : +1 or. */
    private fun dock(state: GameState, index: Int): GameState =
        state.copy(islandHolder = index)
            .updateCaptain(index) { it.copy(gold = it.gold + GameRules.ISLAND_DOCK_BONUS) }

    private fun GameState.updateCaptain(index: Int, transform: (Captain) -> Captain): GameState =
        copy(captains = captains.mapIndexed { i, c -> if (i == index) transform(c) else c })
}
