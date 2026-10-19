package com.example.ileauxtresors.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ileauxtresors.model.ButinCard
import com.example.ileauxtresors.model.GameEngine
import com.example.ileauxtresors.model.GameRules
import com.example.ileauxtresors.model.GameState
import com.example.ileauxtresors.model.Ship
import com.example.ileauxtresors.navigation.PageGroup
import com.example.ileauxtresors.ui.components.PageScaffold
import com.example.ileauxtresors.ui.theme.IleAuxTresorsTheme

/** Page.TAVERNE : 3 cartes Butin, mes gourdes. Acheter, Balayer, Fin du tour → PLATEAU. */
@Composable
fun TavernePage(
    game: GameState,
    onAcheter: (cardIndex: Int) -> Unit,
    onBalayer: () -> Unit,
    onFinDuTour: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PageScaffold(title = "Taverne du Butin", group = PageGroup.PARTIE, modifier = modifier) {
        Text(
            "Mes gourdes : ${game.player.gourds} · Or : ${game.player.gold}/${GameRules.GOLD_TO_WIN}",
            style = MaterialTheme.typography.titleMedium,
        )

        if (game.tavernCards.isEmpty()) Text("La taverne n'a plus de cartes.")
        game.tavernCards.forEachIndexed { i, card ->
            ButinCardView(card, canBuy = GameEngine.canBuy(game, i), onAcheter = { onAcheter(i) })
        }

        OutlinedButton(
            onClick = onBalayer,
            enabled = game.player.gourds >= GameRules.SWEEP_COST,
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Balayer les cartes (${GameRules.SWEEP_COST} gourdes)") }

        Button(onClick = onFinDuTour, modifier = Modifier.fillMaxWidth()) { Text("Fin du tour") }
    }
}

@Composable
private fun ButinCardView(card: ButinCard, canBuy: Boolean, onAcheter: () -> Unit) {
    OutlinedCard(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(Modifier.weight(1f)) {
                Text(card.name, style = MaterialTheme.typography.titleSmall)
                Text(card.description, style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = onAcheter, enabled = canBuy) { Text("${card.cost} gourdes") }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TavernePagePreview() {
    val game = GameState.newGame("Capitaine Amina", Ship.MAREE_BLEUE).let { g ->
        g.copy(captains = g.captains.mapIndexed { i, c -> if (i == 0) c.copy(gourds = 4) else c })
    }
    IleAuxTresorsTheme { TavernePage(game, onAcheter = {}, onBalayer = {}, onFinDuTour = {}) }
}
