package com.example.ileauxtresors.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ileauxtresors.model.GameEngine
import com.example.ileauxtresors.model.GameRules
import com.example.ileauxtresors.model.GameState
import com.example.ileauxtresors.model.RankedCaptain
import com.example.ileauxtresors.model.Ship
import com.example.ileauxtresors.navigation.PageGroup
import com.example.ileauxtresors.ui.components.PageScaffold
import com.example.ileauxtresors.ui.theme.Gold
import com.example.ileauxtresors.ui.theme.IleAuxTresorsTheme

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
    PageScaffold(title = if (won) "Victoire !" else "Défaite", group = PageGroup.FIN, modifier = modifier) {
        Text(endReason(game), style = MaterialTheme.typography.bodyLarge)
        Text("Classement", style = MaterialTheme.typography.titleMedium)
        GameEngine.ranking(game).forEach { RankingRow(it, isPlayer = it.captain == game.player) }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onRejouer, modifier = Modifier.weight(1f)) { Text("Rejouer") }
            OutlinedButton(onClick = onMenu, modifier = Modifier.weight(1f)) { Text("Menu") }
        }
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

/** Une ligne du classement. Le joueur est mis en avant, les ex aequo gardent le même rang. */
@Composable
private fun RankingRow(ranked: RankedCaptain, isPlayer: Boolean) {
    val c = ranked.captain
    val status = if (c.isSunk) "coulé" else "${c.gold} or · coque ${c.hull}/${GameRules.MAX_HULL}"
    val name = if (isPlayer) "${c.name} (toi)" else c.name
    Card(
        colors = if (isPlayer) CardDefaults.cardColors(containerColor = Gold) else CardDefaults.cardColors(),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "${ranked.rank}. $name — $status",
            fontWeight = if (isPlayer) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(12.dp),
        )
    }
}

@Preview(showBackground = true)
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
