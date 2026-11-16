package com.example.ileauxtresors.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ileauxtresors.bots.BotLog
import com.example.ileauxtresors.bots.BotPlayer
import com.example.ileauxtresors.model.GameEngine
import com.example.ileauxtresors.model.GameState
import com.example.ileauxtresors.model.Ship
import com.example.ileauxtresors.navigation.Page
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

    /**
     * Ce que le bot vient de faire, affiché jusqu'à ce que le joueur appuie sur « Continuer ».
     * Null pendant que le bot lance ses dés, et pendant le tour du joueur.
     */
    var botSummary by mutableStateOf<String?>(null)
        private set

    /** Vrai tant que la popup « Quitter la partie ? » est affichée. */
    var confirmQuit by mutableStateOf(false)
        private set

    /** La page à retrouver en quittant les règles (l'accueil, ou la partie via le menu ☰). */
    private var rulesReturnPage = Page.ACCUEIL

    private var botsJob: Job? = null

    /** Débloque le bot en attente de « Continuer ». */
    private var continueSignal: CompletableDeferred<Unit>? = null

    fun goToPage(target: Page) {
        page = target
    }

    fun openRules() {
        rulesReturnPage = page
        goToPage(Page.REGLES)
    }

    fun closeRules() = goToPage(rulesReturnPage)

    /** Bouton retour d'Android. Sur la popup ATTAQUE, il faut répondre : le retour ne fait rien. */
    fun onBack() {
        when (page) {
            Page.ACCUEIL, Page.ATTAQUE -> Unit
            Page.REGLES -> closeRules()
            Page.CAPITAINE -> goToPage(Page.ACCUEIL)
            Page.PLATEAU, Page.TAVERNE -> askQuit()
            Page.FIN -> backToMenu()
        }
    }

    fun askQuit() {
        confirmQuit = true
    }

    fun cancelQuit() {
        confirmQuit = false
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
        if (botsJob?.isActive == true) return
        botLog = emptyList()
        playBots(GameEngine.nextTurn(current))
    }

    /** Réponse du joueur à la popup ATTAQUE, puis les bots suivants finissent leur tour. */
    fun answerAttack(flee: Boolean) {
        val current = game ?: return
        playBots(GameEngine.nextTurn(GameEngine.answerIslandAttack(current, flee)))
    }

    /** Le joueur a lu le résumé du bot : on passe au joueur suivant. */
    fun continueBots() {
        continueSignal?.complete(Unit)
    }

    fun replay() {
        stopGame()
        goToPage(Page.CAPITAINE)
    }

    fun backToMenu() {
        stopGame()
        goToPage(Page.ACCUEIL)
    }

    private fun stopGame() {
        botsJob?.cancel()
        botSummary = null
        confirmQuit = false
        game = null
    }

    /**
     * Fait jouer les bots un par un jusqu'au tour du joueur. On voit chaque lancer et les dés
     * gardés, puis le résumé du tour reste affiché jusqu'à « Continuer ».
     * S'arrête sur ATTAQUE si un bot touche le joueur sur l'île, ou sur FIN si la partie est terminée.
     */
    private fun playBots(start: GameState) {
        botsJob = viewModelScope.launch {
            var state = start
            game = state
            goToPage(Page.PLATEAU)
            while (state.current.isBot && !isOver(state)) {
                pause(BOT_INTRO_MS)
                val before = state
                val steps = BotPlayer.playTurnSteps(state)
                steps.dropLast(1).forEach {
                    game = it
                    pause(BOT_STEP_MS)
                }
                state = answerBotIslandHit(steps.last())
                val summary = BotLog.describeTurn(before, state)
                botLog = botLog + summary
                game = state
                if (isOver(state)) break
                if (state.islandHit != null) {
                    goToPage(Page.ATTAQUE)
                    return@launch
                }
                waitForContinue(summary)
                state = GameEngine.nextTurn(state)
                game = state
            }
            if (!goToFinIfOver()) goToPage(Page.PLATEAU)
        }
    }

    /** Attend [ms], puis encore tant que la popup « Quitter ? » est ouverte. */
    private suspend fun pause(ms: Long) {
        delay(ms)
        snapshotFlow { confirmQuit }.first { !it }
    }

    private suspend fun waitForContinue(summary: String) {
        val signal = CompletableDeferred<Unit>()
        continueSignal = signal
        botSummary = summary
        signal.await()
        botSummary = null
        continueSignal = null
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

    private companion object {
        /** Pause sur « Tour de X » avant son premier lancer. */
        const val BOT_INTRO_MS = 1000L
        /** Pause sur chaque lancer et chaque choix de dés gardés. */
        const val BOT_STEP_MS = 1100L
    }
}
