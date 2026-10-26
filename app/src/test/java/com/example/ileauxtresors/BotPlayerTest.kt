package com.example.ileauxtresors

import com.example.ileauxtresors.bots.BotPlayer
import com.example.ileauxtresors.model.ButinCard
import com.example.ileauxtresors.model.ButinEffect
import com.example.ileauxtresors.model.Captain
import com.example.ileauxtresors.model.DieFace
import com.example.ileauxtresors.model.GameEngine
import com.example.ileauxtresors.model.GameRules
import com.example.ileauxtresors.model.GameState
import com.example.ileauxtresors.model.Ship
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class BotPlayerTest {

    /** C'est au premier bot de jouer. */
    private val botTurn = GameState.newGame("Amina", Ship.MAREE_BLEUE).copy(currentIndex = 1)

    private fun GameState.withBot(transform: (Captain) -> Captain) =
        copy(captains = captains.mapIndexed { i, c -> if (i == currentIndex) transform(c) else c })

    @Test
    fun chooseKeptDice_keepsCoinPairsAndGourdsButNotLoneCoins() {
        val s = botTurn.copy(dice = listOf(DieFace.OR, DieFace.OR, DieFace.CUIVRE, DieFace.GOURDE, DieFace.ARGENT, DieFace.CANON))
        // L'île est libre : les canons ne servent à rien.
        assertEquals(setOf(0, 1, 3), BotPlayer.chooseKeptDice(s))
    }

    @Test
    fun chooseKeptDice_keepsOnlyTheCarpentersItNeedsAtSea() {
        val s = botTurn.withBot { it.copy(hull = GameRules.MAX_HULL - 1) }
            .copy(dice = List(3) { DieFace.CHARPENTIER } + List(3) { DieFace.CUIVRE })
        assertEquals(setOf(0, 3, 4, 5), BotPlayer.chooseKeptDice(s))
    }

    @Test
    fun chooseKeptDice_ignoresCarpentersOnIslandButKeepsCannons() {
        val s = botTurn.withBot { it.copy(hull = 3) }
            .copy(islandHolder = 1, dice = listOf(DieFace.CHARPENTIER, DieFace.CANON, DieFace.CANON, DieFace.OR, DieFace.ARGENT, DieFace.CUIVRE))
        assertEquals(setOf(1, 2), BotPlayer.chooseKeptDice(s))
    }

    @Test
    fun chooseKeptDice_attacksIslandOnlyWhenHealthy() {
        val dice = listOf(DieFace.CANON, DieFace.CANON, DieFace.OR, DieFace.ARGENT, DieFace.CUIVRE, DieFace.CHARPENTIER)
        val healthy = botTurn.copy(islandHolder = 0, dice = dice)
        assertTrue(0 in BotPlayer.chooseKeptDice(healthy))

        val weak = healthy.withBot { it.copy(hull = 2) }
        assertFalse(0 in BotPlayer.chooseKeptDice(weak))
    }

    @Test
    fun shouldFlee_staysWhenNextTurnWins() {
        val s = botTurn.withBot { it.copy(gold = GameRules.GOLD_TO_WIN - GameRules.ISLAND_START_BONUS, hull = 4) }
        assertFalse(BotPlayer.shouldFlee(s, 1))
    }

    @Test
    fun shouldFlee_dependsOnHullAndOpponentsAfloat() {
        val damaged = botTurn.withBot { it.copy(hull = 5) }
        assertTrue(BotPlayer.shouldFlee(damaged, 1))

        val oneOpponentLeft = damaged.copy(captains = damaged.captains.mapIndexed { i, c ->
            if (i == 0 || i == 1) c else c.copy(hull = 0)
        })
        assertFalse(BotPlayer.shouldFlee(oneOpponentLeft, 1))
    }

    @Test
    fun cardValue_repairIsWorthlessAtFullHull() {
        val repair = ButinCard("Tonneau de rhum", 3, "", ButinEffect.Repair(2))
        assertEquals(0, BotPlayer.cardValue(botTurn, repair))
        assertEquals(1, BotPlayer.cardValue(botTurn.withBot { it.copy(hull = GameRules.MAX_HULL - 1) }, repair))
    }

    @Test
    fun playTurn_doesNotBuyUselessCards() {
        val repair = ButinCard("Tonneau de rhum", 3, "", ButinEffect.Repair(2))
        val s = botTurn.withBot { it.copy(gourds = 10) }.copy(tavernCards = listOf(repair), deck = emptyList())
        val after = BotPlayer.playTurn(GameEngine.startTurn(s), Random(3))
        // Coque pleine : réparer ne sert à rien, la carte reste à la Taverne.
        assertEquals(listOf(repair), after.tavernCards)
    }

    @Test
    fun playTurn_usesItsRollsAndResolvesDice() {
        repeat(20) { seed ->
            val after = BotPlayer.playTurn(GameEngine.startTurn(botTurn), Random(seed))
            assertEquals(GameRules.DICE_COUNT, after.dice.size)
            assertTrue(after.rollsLeft in 0 until GameRules.ROLLS_PER_TURN)
            // L'île était libre : le bot s'y amarre.
            assertEquals(1, after.islandHolder)
        }
    }

    @Test
    fun playTurnSteps_showsEachRollThenTheResult() {
        repeat(20) { seed ->
            val start = GameEngine.startTurn(botTurn)
            val steps = BotPlayer.playTurnSteps(start, Random(seed))
            assertEquals(BotPlayer.playTurn(start, Random(seed)), steps.last())
            // 1er lancer, puis (dés gardés, relance) autant de fois que nécessaire, puis dés appliqués et achats.
            val rolls = GameRules.ROLLS_PER_TURN - steps.last().rollsLeft
            assertEquals(1 + 2 * (rolls - 1) + 2, steps.size)
            assertEquals(GameRules.ROLLS_PER_TURN - 1, steps.first().rollsLeft)
        }
    }
}
