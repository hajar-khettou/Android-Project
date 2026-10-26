package com.example.ileauxtresors.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ileauxtresors.model.ButinCard
import com.example.ileauxtresors.model.GameEngine
import com.example.ileauxtresors.model.GameRules
import com.example.ileauxtresors.model.GameState
import com.example.ileauxtresors.model.Ship
import com.example.ileauxtresors.ui.components.Backdrop
import com.example.ileauxtresors.ui.components.CoinIcon
import com.example.ileauxtresors.ui.components.GourdBadge
import com.example.ileauxtresors.ui.components.PageHeader
import com.example.ileauxtresors.ui.components.PageScaffold
import com.example.ileauxtresors.ui.components.PirateButton
import com.example.ileauxtresors.ui.components.PirateButtonStyle
import com.example.ileauxtresors.ui.components.PirateCard
import com.example.ileauxtresors.ui.theme.Gold
import com.example.ileauxtresors.ui.theme.IleAuxTresorsTheme
import com.example.ileauxtresors.ui.theme.InkSoft
import com.example.ileauxtresors.ui.theme.Parchment

/** Page.TAVERNE : 3 cartes Butin, mes gourdes. Acheter, Balayer, Fin du tour → PLATEAU. */
@Composable
fun TavernePage(
    game: GameState,
    onAcheter: (cardIndex: Int) -> Unit,
    onBalayer: () -> Unit,
    onFinDuTour: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PageScaffold(
        backdrop = Backdrop.TAVERNE,
        modifier = modifier,
        header = { PageHeader("Taverne du Butin") },
        bottomBar = {
            PirateButton(
                "Balayer les cartes (${GameRules.SWEEP_COST} gourdes)",
                onClick = onBalayer,
                enabled = game.player.gourds >= GameRules.SWEEP_COST,
                style = PirateButtonStyle.CONTOUR,
                modifier = Modifier.fillMaxWidth(),
            )
            PirateButton("Fin du tour", onClick = onFinDuTour, modifier = Modifier.fillMaxWidth())
        },
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GourdBadge(game.player.gourds)
            Spacer(Modifier.width(24.dp))
            CoinIcon(size = 20.dp)
            Text(
                " ${game.player.gold} / ${GameRules.GOLD_TO_WIN}",
                color = Gold,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Text(
            "Dépensez vos gourdes : chaque carte agit tout de suite.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        if (game.tavernCards.isEmpty()) Text("La taverne n'a plus de cartes.")
        game.tavernCards.forEachIndexed { i, card ->
            ButinCardView(card, canBuy = GameEngine.canBuy(game, i), onAcheter = { onAcheter(i) })
        }
    }
}

@Composable
private fun ButinCardView(card: ButinCard, canBuy: Boolean, onAcheter: () -> Unit) {
    PirateCard(color = Parchment) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f)) {
                Text(card.name, style = MaterialTheme.typography.titleLarge)
                Text(card.description, style = MaterialTheme.typography.bodyMedium, color = InkSoft)
            }
            PirateButton(
                "${card.cost} gourdes",
                onClick = onAcheter,
                enabled = canBuy,
                compact = true,
                modifier = Modifier.width(110.dp),
            )
        }
    }
}

@Preview(widthDp = 380, heightDp = 800)
@Composable
private fun TavernePagePreview() {
    val game = GameState.newGame("Capitaine Amina", Ship.MAREE_BLEUE).let { g ->
        g.copy(captains = g.captains.mapIndexed { i, c -> if (i == 0) c.copy(gourds = 4) else c })
    }
    IleAuxTresorsTheme { TavernePage(game, onAcheter = {}, onBalayer = {}, onFinDuTour = {}) }
}
