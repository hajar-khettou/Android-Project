package com.example.ileauxtresors

import com.example.ileauxtresors.bots.BotLog
import com.example.ileauxtresors.model.ButinCard
import com.example.ileauxtresors.model.ButinEffect
import com.example.ileauxtresors.model.DieFace
import com.example.ileauxtresors.model.GameEngine
import com.example.ileauxtresors.model.GameState
import com.example.ileauxtresors.model.Ship
import org.junit.Assert.assertEquals
import org.junit.Test

class BotLogTest {

    /** C'est au premier bot (Anne la Rouge) de jouer. */
    private val botTurn = GameState.newGame("Amina", Ship.MAREE_BLEUE).copy(currentIndex = 1)
    private val bot = botTurn.captains[1].name

    @Test
    fun describeTurn_dockingWithCoinsAndGourds() {
        val before = botTurn.copy(dice = listOf(DieFace.OR, DieFace.OR, DieFace.OR, DieFace.GOURDE, DieFace.GOURDE, DieFace.CUIVRE))
        val after = GameEngine.resolveDice(before)
        assertEquals("$bot : +4 or, +2 gourdes, s'amarre à l'île", BotLog.describeTurn(before, after))
    }

    @Test
    fun describeTurn_attackFromSeaChasesHolder() {
        val before = botTurn.copy(islandHolder = 0, dice = listOf(DieFace.CANON, DieFace.CANON) + List(4) { DieFace.CHARPENTIER })
        val hit = GameEngine.resolveDice(before)
        val after = GameEngine.answerIslandAttack(hit, flee = true)
        assertEquals("$bot : +1 or, touche Amina (−2), chasse Amina de l'île", BotLog.describeTurn(before, after))
    }

    @Test
    fun describeTurn_listsBoughtCards() {
        val card = ButinCard("Coffre enterré", 5, "", ButinEffect.GainGold(3))
        val before = botTurn.copy(
            islandHolder = 2,
            captains = botTurn.captains.mapIndexed { i, c -> if (i == 1) c.copy(gourds = 5) else c },
            tavernCards = listOf(card),
            deck = emptyList(),
        )
        val after = GameEngine.buyCard(before, 0)
        assertEquals("$bot : +3 or, achète « Coffre enterré »", BotLog.describeTurn(before, after))
    }

    @Test
    fun describeTurn_nothingHappened() {
        val before = botTurn.copy(islandHolder = 2)
        assertEquals("$bot : rien de notable", BotLog.describeTurn(before, before))
    }
}
