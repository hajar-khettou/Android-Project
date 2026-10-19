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
import com.example.ileauxtresors.model.GameState
import com.example.ileauxtresors.model.Ship
import com.example.ileauxtresors.navigation.PageGroup
import com.example.ileauxtresors.ui.components.PageScaffold
import com.example.ileauxtresors.ui.theme.IleAuxTresorsTheme

/** Page.TAVERNE : 3 cartes Butin, mes gourdes, mes cartes. Fin du tour → PLATEAU. */
@Composable
fun TavernePage(
    game: GameState,
    onFinDuTour: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PageScaffold(title = "Taverne du Butin", group = PageGroup.PARTIE, modifier = modifier) {
        Text("Mes gourdes : ${game.player.gourds}", style = MaterialTheme.typography.titleMedium)

        // TODO : afficher les 3 cartes visibles (game.tavernCards) quand le paquet sera défini.
        Text("Cartes Butin à venir", style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // TODO : brancher Acheter et Balayer sur le moteur de jeu.
            OutlinedButton(onClick = {}, enabled = false, modifier = Modifier.weight(1f)) { Text("Acheter") }
            OutlinedButton(onClick = {}, enabled = false, modifier = Modifier.weight(1f)) { Text("Balayer") }
        }

        Text("Mes cartes : ${game.player.cards.size}", style = MaterialTheme.typography.titleMedium)

        Button(onClick = onFinDuTour, modifier = Modifier.fillMaxWidth()) { Text("Fin du tour") }
    }
}

@Preview(showBackground = true)
@Composable
private fun TavernePagePreview() {
    IleAuxTresorsTheme {
        TavernePage(GameState.newGame("Capitaine Amina", Ship.MAREE_BLEUE), onFinDuTour = {})
    }
}
