package com.example.ileauxtresors.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
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

    /** Index du capitaine qui vient de toucher le joueur sur l'île (pour la popup ATTAQUE). */
    private var pendingAttacker: Int? = null

    fun goToPage(target: Page) {
        page = target
    }

    fun startGame(playerName: String, ship: Ship) {
        game = GameEngine.startTurn(GameState.newGame(playerName, ship))
        goToPage(Page.PLATEAU)
    }

    fun rollDice() = update { GameEngine.rollDice(it) }

    fun toggleKeep(index: Int) = update { GameEngine.toggleKeep(it, index) }

    fun validateDice() {
        update { GameEngine.resolveDice(it) }
        if (!goToFinIfOver()) goToPage(Page.TAVERNE)
    }

    /** Fin du tour du joueur : les bots jouent, puis on revient au joueur. */
    fun endTurn() {
        update { playBots(GameEngine.nextTurn(it)) }
        // TODO : si un bot a touché le joueur sur l'île, remplir pendingAttacker
        //  et aller sur Page.ATTAQUE au lieu de PLATEAU.
        if (!goToFinIfOver()) goToPage(Page.PLATEAU)
    }

    fun stayOnIsland() {
        pendingAttacker = null
        goToPage(Page.PLATEAU)
    }

    fun fleeIsland() {
        pendingAttacker?.let { attacker -> update { GameEngine.fleeIsland(it, attacker) } }
        pendingAttacker = null
        goToPage(Page.PLATEAU)
    }

    fun replay() {
        game = null
        goToPage(Page.CAPITAINE)
    }

    fun backToMenu() {
        game = null
        goToPage(Page.ACCUEIL)
    }

    private fun playBots(start: GameState): GameState {
        var state = start
        while (state.current.isBot && !isOver(state)) {
            state = GameEngine.nextTurn(BotPlayer.playTurn(state))
        }
        return state
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
