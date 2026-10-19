package com.example.ileauxtresors

import com.example.ileauxtresors.model.DieFace
import com.example.ileauxtresors.model.GameEngine
import com.example.ileauxtresors.model.GameRules
import com.example.ileauxtresors.model.GameState
import com.example.ileauxtresors.model.Ship
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GameEngineTest {

    private val newGame = GameState.newGame("Amina", Ship.MAREE_BLEUE)

    @Test
    fun newGame_givesEachCaptainADifferentShip() {
        assertEquals(GameRules.PLAYER_COUNT, newGame.captains.size)
        assertEquals(Ship.MAREE_BLEUE, newGame.player.ship)
        assertEquals(Ship.entries.toSet(), newGame.captains.map { it.ship }.toSet())
        assertTrue(newGame.bots.all { it.isBot })
    }

    @Test
    fun rollDice_keepsKeptDiceAndUsesARoll() {
        val first = GameEngine.rollDice(newGame, Random(1))
        assertEquals(GameRules.DICE_COUNT, first.dice.size)
        assertEquals(GameRules.ROLLS_PER_TURN - 1, first.rollsLeft)

        val kept = GameEngine.toggleKeep(first, 0)
        val second = GameEngine.rollDice(kept, Random(2))
        assertEquals(first.dice[0], second.dice[0])
    }

    @Test
    fun rollDice_doesNothingWithoutRollsLeft() {
        val noRolls = newGame.copy(dice = List(6) { DieFace.OR }, rollsLeft = 0)
        assertEquals(noRolls, GameEngine.rollDice(noRolls))
    }

    @Test
    fun winner_isFirstToReachGoldGoalOrLastAfloat() {
        assertNull(GameEngine.winner(newGame))

        val rich = newGame.copy(captains = newGame.captains.mapIndexed { i, c ->
            if (i == 2) c.copy(gold = GameRules.GOLD_TO_WIN) else c
        })
        assertEquals(rich.captains[2], GameEngine.winner(rich))

        val lastAfloat = newGame.copy(captains = newGame.captains.mapIndexed { i, c ->
            if (i == 0) c else c.copy(hull = 0)
        })
        assertEquals(lastAfloat.player, GameEngine.winner(lastAfloat))
    }

    @Test
    fun nextTurn_skipsSunkCaptains() {
        val state = newGame.copy(captains = newGame.captains.mapIndexed { i, c ->
            if (i == 1) c.copy(hull = 0) else c
        })
        assertEquals(2, GameEngine.nextTurn(state).currentIndex)
    }
}
