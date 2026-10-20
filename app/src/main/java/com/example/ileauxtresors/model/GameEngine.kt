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

    fun canBuy(state: GameState, cardIndex: Int): Boolean {
        val card = state.tavernCards.getOrNull(cardIndex) ?: return false
        return state.current.gourds >= card.cost
    }

    /** Le joueur courant achète une carte visible : il paie en gourdes, l'effet s'applique, la carte est remplacée. */
    fun buyCard(state: GameState, cardIndex: Int): GameState {
        if (!canBuy(state, cardIndex)) return state
        val card = state.tavernCards[cardIndex]
        val paid = state.updateCaptain(state.currentIndex) { it.copy(gourds = it.gourds - card.cost) }
        val refilled = paid.copy(
            tavernCards = paid.tavernCards.filterIndexed { i, _ -> i != cardIndex } + paid.deck.take(1),
            deck = paid.deck.drop(1),
        )
        return applyEffect(refilled, card.effect)
    }

    /** Balayer : le joueur courant paie [GameRules.SWEEP_COST] gourdes pour remplacer les cartes visibles. */
    fun sweepTavern(state: GameState): GameState {
        if (state.current.gourds < GameRules.SWEEP_COST) return state
        val paid = state.updateCaptain(state.currentIndex) { it.copy(gourds = it.gourds - GameRules.SWEEP_COST) }
        // Les cartes balayées repartent sous la pioche.
        val pile = paid.deck + paid.tavernCards
        return paid.copy(
            tavernCards = pile.take(GameRules.TAVERN_VISIBLE_CARDS),
            deck = pile.drop(GameRules.TAVERN_VISIBLE_CARDS),
        )
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

    /**
     * Le classement de fin : le vainqueur, puis les navires à flot, puis les coulés.
     * À égalité, le plus d'or passe devant, puis la coque la plus solide.
     */
    fun ranking(state: GameState): List<RankedCaptain> {
        val winner = winner(state)
        val order = compareBy<Captain>({ it != winner }, { it.isSunk }, { -it.gold }, { -it.hull })
        val sorted = state.captains.sortedWith(order)
        var rank = 0
        return sorted.mapIndexed { i, c ->
            if (i == 0 || order.compare(sorted[i - 1], c) != 0) rank = i + 1
            RankedCaptain(rank, c)
        }
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

    private fun applyEffect(state: GameState, effect: ButinEffect): GameState {
        val me = state.currentIndex
        return when (effect) {
            is ButinEffect.GainGold -> state.updateCaptain(me) { it.copy(gold = it.gold + effect.amount) }
            is ButinEffect.Repair ->
                state.updateCaptain(me) { it.copy(hull = minOf(GameRules.MAX_HULL, it.hull + effect.amount)) }
            is ButinEffect.DamageOthers -> {
                val damaged = state.copy(captains = state.captains.mapIndexed { i, c ->
                    if (i == me || c.isSunk) c else c.damaged(effect.amount)
                })
                // Un capitaine coulé libère l'île.
                val holder = damaged.islandHolder
                if (holder != null && damaged.captains[holder].isSunk) damaged.copy(islandHolder = null, islandHit = null)
                else damaged
            }
            is ButinEffect.Plunder -> {
                var taken = 0
                val robbed = state.copy(captains = state.captains.mapIndexed { i, c ->
                    if (i == me || c.isSunk) return@mapIndexed c
                    val loss = minOf(effect.amount, c.gold)
                    taken += loss
                    c.copy(gold = c.gold - loss)
                })
                robbed.updateCaptain(me) { it.copy(gold = it.gold + taken) }
            }
        }
    }

    /** Le capitaine [index] s'amarre à l'île : +1 or. */
    private fun dock(state: GameState, index: Int): GameState =
        state.copy(islandHolder = index)
            .updateCaptain(index) { it.copy(gold = it.gold + GameRules.ISLAND_DOCK_BONUS) }

    private fun GameState.updateCaptain(index: Int, transform: (Captain) -> Captain): GameState =
        copy(captains = captains.mapIndexed { i, c -> if (i == index) transform(c) else c })
}
