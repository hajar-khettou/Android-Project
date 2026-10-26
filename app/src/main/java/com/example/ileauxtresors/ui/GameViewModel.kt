package com.example.ileauxtresors.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.ileauxtresors.bots.BotLog
import com.example.ileauxtresors.bots.BotPlayer
import com.example.ileauxtresors.model.GameEngine
import com.example.ileauxtresors.model.GameState
import com.example.ileauxtresors.model.Ship
import com.example.ileauxtresors.navigation.Page

/**
 * Fait le lien entre les pages et le moteur de jeu.
 * Garde la page affichée et l'état de la partie (survit à la rotation de l'écran).
 */
class GameViewModel : ViewModel() {

    var page by mutableStateOf(Page.ACCUEIL)
        private set

    var game by mutableStateOf<GameState?>(null)
        private set

    /** Ce que les bots ont fait depuis le dernier tour du joueur, une ligne par tour de bot. */
    var botLog by mutableStateOf<List<String>>(emptyList())
        private set

    fun goToPage(target: Page) {
        page = target
    }

    fun startGame(playerName: String, ship: Ship) {
        game = GameEngine.startTurn(GameState.newGame(playerName, ship))
        botLog = emptyList()
        goToPage(Page.PLATEAU)
    }

    fun rollDice() = update { GameEngine.rollDice(it) }

    fun toggleKeep(index: Int) = update { GameEngine.toggleKeep(it, index) }

    fun validateDice() {
        update { answerBotIslandHit(GameEngine.resolveDice(it)) }
        if (!goToFinIfOver()) goToPage(Page.TAVERNE)
    }

    fun buyCard(index: Int) {
        update { GameEngine.buyCard(it, index) }
        goToFinIfOver()
    }

    fun sweepTavern() = update { GameEngine.sweepTavern(it) }

    /** Fin du tour du joueur : les bots jouent, puis on revient au joueur. */
    fun endTurn() {
        val current = game ?: return
        botLog = emptyList()
        playBots(GameEngine.nextTurn(current))
    }

    /** Réponse du joueur à la popup ATTAQUE, puis les bots suivants finissent leur tour. */
    fun answerAttack(flee: Boolean) {
        val current = game ?: return
        playBots(GameEngine.nextTurn(GameEngine.answerIslandAttack(current, flee)))
    }

    fun replay() {
        game = null
        goToPage(Page.CAPITAINE)
    }

    fun backToMenu() {
        game = null
        goToPage(Page.ACCUEIL)
    }

    /**
     * Fait jouer les bots jusqu'au tour du joueur. S'arrête sur ATTAQUE si un bot
     * touche le joueur sur l'île, ou sur FIN si la partie est terminée.
     */
    private fun playBots(start: GameState) {
        var state = start
        while (state.current.isBot && !isOver(state)) {
            val before = state
            state = answerBotIslandHit(BotPlayer.playTurn(state))
            botLog = botLog + BotLog.describeTurn(before, state)
            if (isOver(state)) break
            if (state.islandHit != null) {
                game = state
                goToPage(Page.ATTAQUE)
                return
            }
            state = GameEngine.nextTurn(state)
        }
        game = state
        if (!goToFinIfOver()) goToPage(Page.PLATEAU)
    }

    /** Si c'est un bot qui est touché sur l'île, il décide tout de suite. */
    private fun answerBotIslandHit(state: GameState): GameState {
        val holder = state.islandHolder
        if (state.islandHit == null || holder == null || !state.captains[holder].isBot) return state
        return GameEngine.answerIslandAttack(state, BotPlayer.shouldFlee(state, holder))
    }

    private fun isOver(state: GameState) = GameEngine.winner(state) != null || state.player.isSunk

    private fun goToFinIfOver(): Boolean {
        val over = game?.let(::isOver) ?: false
        if (over) goToPage(Page.FIN)
        return over
    }

    private inline fun update(transform: (GameState) -> GameState) {
        game = game?.let(transform)
    }
}
