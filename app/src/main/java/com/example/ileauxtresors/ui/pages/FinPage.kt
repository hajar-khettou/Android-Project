package com.example.ileauxtresors.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ileauxtresors.model.GameEngine
import com.example.ileauxtresors.model.GameState
import com.example.ileauxtresors.model.Ship
import com.example.ileauxtresors.navigation.PageGroup
import com.example.ileauxtresors.ui.components.PageScaffold
import com.example.ileauxtresors.ui.theme.IleAuxTresorsTheme

/** Page.FIN : victoire ou défaite, classement. Rejouer → CAPITAINE · Menu → ACCUEIL. */
@Composable
fun FinPage(
    game: GameState,
    onRejouer: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val won = GameEngine.winner(game) == game.player
    PageScaffold(title = if (won) "Victoire !" else "Défaite", group = PageGroup.FIN, modifier = modifier) {
        Text("Classement", style = MaterialTheme.typography.titleMedium)
        // TODO : départager les égalités, mettre en avant le joueur.
        game.captains
            .sortedWith(compareBy({ it.isSunk }, { -it.gold }))
            .forEachIndexed { rank, c ->
                val status = if (c.isSunk) "coulé" else "${c.gold} or"
                Text("${rank + 1}. ${c.name} — $status")
            }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onRejouer, modifier = Modifier.weight(1f)) { Text("Rejouer") }
            OutlinedButton(onClick = onMenu, modifier = Modifier.weight(1f)) { Text("Menu") }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FinPagePreview() {
    val game = GameState.newGame("Capitaine Amina", Ship.MAREE_BLEUE).let { g ->
        g.copy(captains = g.captains.mapIndexed { i, c -> c.copy(gold = if (i == 0) 20 else 5 * i) })
    }
    IleAuxTresorsTheme { FinPage(game, onRejouer = {}, onMenu = {}) }
}
