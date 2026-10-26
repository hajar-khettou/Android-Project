package com.example.ileauxtresors.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ileauxtresors.model.GameEngine
import com.example.ileauxtresors.model.GameRules
import com.example.ileauxtresors.model.GameState
import com.example.ileauxtresors.model.RankedCaptain
import com.example.ileauxtresors.model.Ship
import com.example.ileauxtresors.ui.components.Backdrop
import com.example.ileauxtresors.ui.components.CaptainAvatar
import com.example.ileauxtresors.ui.components.PageScaffold
import com.example.ileauxtresors.ui.components.PirateButton
import com.example.ileauxtresors.ui.components.PirateButtonStyle
import com.example.ileauxtresors.ui.components.PirateCard
import com.example.ileauxtresors.ui.theme.Cream
import com.example.ileauxtresors.ui.theme.Gold
import com.example.ileauxtresors.ui.theme.IleAuxTresorsTheme
import com.example.ileauxtresors.ui.theme.InkSoft
import com.example.ileauxtresors.ui.theme.Parchment

/** Page.FIN : victoire ou défaite, classement. Rejouer → CAPITAINE · Menu → ACCUEIL. */
@Composable
fun FinPage(
    game: GameState,
    onRejouer: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val winner = GameEngine.winner(game)
    val won = winner == game.player
    PageScaffold(
        backdrop = Backdrop.NUIT,
        modifier = modifier,
        bottomBar = {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PirateButton("Menu", onClick = onMenu, style = PirateButtonStyle.CONTOUR, modifier = Modifier.weight(1f))
                PirateButton("Rejouer", onClick = onRejouer, modifier = Modifier.weight(1f))
            }
        },
    ) {
        Spacer(Modifier.height(32.dp))
        Text(
            if (won) "Victoire !" else "Défaite",
            color = if (won) Gold else Cream,
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            endReason(game),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Text("Classement", style = MaterialTheme.typography.titleLarge)
        GameEngine.ranking(game).forEach { RankingRow(it, isPlayer = it.captain == game.player, isWinner = it.captain == winner) }
    }
}

/** Pourquoi la partie s'est arrêtée, en une phrase. */
private fun endReason(game: GameState): String {
    val winner = GameEngine.winner(game)
    return when {
        winner == null -> "Ton navire a coulé. La partie continue sans toi…"
        winner.gold >= GameRules.GOLD_TO_WIN ->
            if (winner == game.player) "Tu as amassé ${GameRules.GOLD_TO_WIN} pièces d'or !"
            else "${winner.name} a amassé ${GameRules.GOLD_TO_WIN} pièces d'or."
        else ->
            if (winner == game.player) "Ton navire est le dernier à flot !"
            else "${winner.name} est le dernier navire à flot."
    }
}

/** Une ligne du classement. Le joueur est sur parchemin, les ex aequo gardent le même rang. */
@Composable
private fun RankingRow(ranked: RankedCaptain, isPlayer: Boolean, isWinner: Boolean) {
    val c = ranked.captain
    val status = if (c.isSunk) "Coulé" else "${c.gold} or · coque ${c.hull}/${GameRules.MAX_HULL}"
    PirateCard(
        modifier = Modifier.alpha(if (c.isSunk) 0.55f else 1f),
        color = if (isPlayer) Parchment else Color.White.copy(alpha = 0.08f),
        contentColor = if (isPlayer) MaterialTheme.colorScheme.onSurface else Cream,
        selected = isWinner,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${ranked.rank}", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.width(32.dp))
            CaptainAvatar(c)
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
            ) {
                Text(
                    (if (isWinner) "♛ " else "") + c.name + if (isPlayer) " (toi)" else "",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(status, style = MaterialTheme.typography.bodySmall, color = if (isPlayer) InkSoft else Cream.copy(alpha = 0.7f))
            }
        }
    }
}

@Preview(widthDp = 380, heightDp = 800)
@Composable
private fun FinPagePreview() {
    val game = GameState.newGame("Capitaine Amina", Ship.MAREE_BLEUE).let { g ->
        g.copy(captains = g.captains.mapIndexed { i, c ->
            when (i) {
                0 -> c.copy(gold = 20)
                1 -> c.copy(hull = 0)
                else -> c.copy(gold = 8, hull = 6)
            }
        })
    }
    IleAuxTresorsTheme { FinPage(game, onRejouer = {}, onMenu = {}) }
}
