package com.example.ileauxtresors

import com.example.ileauxtresors.model.ButinCard
import com.example.ileauxtresors.model.ButinDeck
import com.example.ileauxtresors.model.ButinEffect
import com.example.ileauxtresors.model.DieFace
import com.example.ileauxtresors.model.GameEngine
import com.example.ileauxtresors.model.GameRules
import com.example.ileauxtresors.model.GameState
import com.example.ileauxtresors.model.IslandHit
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

    private fun withDice(vararg faces: DieFace, current: Int = 0, holder: Int? = null) =
        newGame.copy(dice = faces.toList(), currentIndex = current, islandHolder = holder)

    @Test
    fun coinGold_needsThreeOfAKindPlusOnePerExtra() {
        assertEquals(0, GameEngine.coinGold(listOf(DieFace.OR, DieFace.OR, DieFace.ARGENT)))
        assertEquals(1, GameEngine.coinGold(List(3) { DieFace.CUIVRE }))
        assertEquals(4, GameEngine.coinGold(List(4) { DieFace.OR } + DieFace.CUIVRE + DieFace.CANON))
        assertEquals(1 + 2, GameEngine.coinGold(List(3) { DieFace.CUIVRE } + List(3) { DieFace.ARGENT }))
    }

    @Test
    fun resolveDice_onFreeIslandDocksAndAddsGourds() {
        val s = GameEngine.resolveDice(withDice(DieFace.GOURDE, DieFace.GOURDE, DieFace.OR))
        assertEquals(0, s.islandHolder)
        assertEquals(GameRules.ISLAND_DOCK_BONUS, s.player.gold)
        assertEquals(2, s.player.gourds)
    }

    @Test
    fun resolveDice_carpenterRepairsAtSeaButNotOnIsland() {
        val damaged = newGame.copy(captains = newGame.captains.map { it.damaged(3) })
        val atSea = GameEngine.resolveDice(damaged.copy(dice = List(2) { DieFace.CHARPENTIER }, islandHolder = 1))
        assertEquals(GameRules.MAX_HULL - 1, atSea.player.hull)

        val onIsland = GameEngine.resolveDice(damaged.copy(dice = List(2) { DieFace.CHARPENTIER }, islandHolder = 0))
        assertEquals(GameRules.MAX_HULL - 3, onIsland.player.hull)

        val full = GameEngine.resolveDice(withDice(*Array(4) { DieFace.CHARPENTIER }, holder = 1))
        assertEquals(GameRules.MAX_HULL, full.player.hull)
    }

    @Test
    fun resolveDice_cannonsFromIslandHitEveryoneElse() {
        val s = GameEngine.resolveDice(withDice(DieFace.CANON, DieFace.CANON, holder = 0))
        assertEquals(GameRules.MAX_HULL, s.player.hull)
        assertTrue(s.bots.all { it.hull == GameRules.MAX_HULL - 2 })
        assertNull(s.islandHit)
    }

    @Test
    fun resolveDice_cannonsAtSeaHitIslandHolderWhoMustAnswer() {
        val s = GameEngine.resolveDice(withDice(DieFace.CANON, current = 2, holder = 0))
        assertEquals(GameRules.MAX_HULL - 1, s.player.hull)
        assertEquals(GameRules.MAX_HULL, s.captains[1].hull)
        assertEquals(IslandHit(attackerIndex = 2, damage = 1), s.islandHit)
        assertEquals(0, s.islandHolder)

        val stayed = GameEngine.answerIslandAttack(s, flee = false)
        assertNull(stayed.islandHit)
        assertEquals(0, stayed.islandHolder)

        val fled = GameEngine.answerIslandAttack(s, flee = true)
        assertNull(fled.islandHit)
        assertEquals(2, fled.islandHolder)
        assertEquals(GameRules.ISLAND_DOCK_BONUS, fled.captains[2].gold)
    }

    @Test
    fun resolveDice_sinkingTheIslandHolderLetsTheAttackerDock() {
        val weak = newGame.copy(captains = newGame.captains.mapIndexed { i, c -> if (i == 1) c.copy(hull = 1) else c })
        val s = GameEngine.resolveDice(weak.copy(dice = List(2) { DieFace.CANON }, islandHolder = 1))
        assertTrue(s.captains[1].isSunk)
        assertNull(s.islandHit)
        assertEquals(0, s.islandHolder)
    }

    @Test
    fun startTurn_givesBonusOnlyToTheIslandHolder() {
        assertEquals(GameRules.ISLAND_START_BONUS, GameEngine.startTurn(newGame.copy(islandHolder = 0)).player.gold)
        assertEquals(0, GameEngine.startTurn(newGame.copy(islandHolder = 1)).player.gold)
    }

    private fun withPlayerGourds(gourds: Int, cards: List<ButinCard>, deck: List<ButinCard> = emptyList()) =
        newGame.copy(
            captains = newGame.captains.mapIndexed { i, c -> if (i == 0) c.copy(gourds = gourds) else c },
            tavernCards = cards,
            deck = deck,
        )

    private fun card(cost: Int, effect: ButinEffect) = ButinCard("Test", cost, "", effect)

    @Test
    fun newGame_showsThreeTavernCards() {
        assertEquals(GameRules.TAVERN_VISIBLE_CARDS, newGame.tavernCards.size)
        assertEquals(ButinDeck.cards.size, newGame.tavernCards.size + newGame.deck.size)
    }

    @Test
    fun buyCard_paysAppliesEffectAndRefills() {
        val gold = card(3, ButinEffect.GainGold(2))
        val next = card(1, ButinEffect.Repair(1))
        val s = GameEngine.buyCard(withPlayerGourds(4, listOf(gold), deck = listOf(next)), 0)
        assertEquals(1, s.player.gourds)
        assertEquals(2, s.player.gold)
        assertEquals(listOf(next), s.tavernCards)
        assertTrue(s.deck.isEmpty())
    }

    @Test
    fun buyCard_refusedWithoutEnoughGourds() {
        val state = withPlayerGourds(2, listOf(card(3, ButinEffect.GainGold(2))))
        assertEquals(state, GameEngine.buyCard(state, 0))
    }

    @Test
    fun buyCard_damageAndPlunderHitOnlyOthers() {
        val rich = withPlayerGourds(10, listOf(card(1, ButinEffect.DamageOthers(2)), card(1, ButinEffect.Plunder(1))))
            .let { s -> s.copy(captains = s.captains.mapIndexed { i, c -> if (i == 1) c.copy(gold = 3) else c }) }
        val damaged = GameEngine.buyCard(rich, 0)
        assertEquals(GameRules.MAX_HULL, damaged.player.hull)
        assertTrue(damaged.bots.all { it.hull == GameRules.MAX_HULL - 2 })

        val plundered = GameEngine.buyCard(damaged, 0)
        assertEquals(1, plundered.player.gold)
        assertEquals(2, plundered.captains[1].gold)
    }

    @Test
    fun sweepTavern_replacesVisibleCardsAndCostsGourds() {
        val state = withPlayerGourds(GameRules.SWEEP_COST, newGame.tavernCards, newGame.deck)
        val s = GameEngine.sweepTavern(state)
        assertEquals(0, s.player.gourds)
        assertEquals(state.deck.take(3), s.tavernCards)
        assertEquals(state.tavernCards, s.deck.takeLast(3))
    }

    @Test
    fun nextTurn_skipsSunkCaptains() {
        val state = newGame.copy(captains = newGame.captains.mapIndexed { i, c ->
            if (i == 1) c.copy(hull = 0) else c
        })
        assertEquals(2, GameEngine.nextTurn(state).currentIndex)
    }
}
