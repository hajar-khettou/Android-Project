package com.example.ileauxtresors.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ileauxtresors.model.ButinCard
import com.example.ileauxtresors.model.DieFace
import com.example.ileauxtresors.model.GameEngine
import com.example.ileauxtresors.model.GameRules
import com.example.ileauxtresors.model.GameState
import com.example.ileauxtresors.model.Ship
import com.example.ileauxtresors.ui.components.Backdrop
import com.example.ileauxtresors.ui.components.CoinIcon
import com.example.ileauxtresors.ui.components.GourdBadge
import com.example.ileauxtresors.ui.components.PageHeader
import com.example.ileauxtresors.ui.components.PageScaffold
import com.example.ileauxtresors.ui.components.Pill
import com.example.ileauxtresors.ui.components.PirateButton
import com.example.ileauxtresors.ui.components.PirateButtonStyle
import com.example.ileauxtresors.ui.components.PirateCard
import com.example.ileauxtresors.ui.theme.Cream
import com.example.ileauxtresors.ui.theme.Gold
import com.example.ileauxtresors.ui.theme.IleAuxTresorsTheme
import com.example.ileauxtresors.ui.theme.Ink
import com.example.ileauxtresors.ui.theme.InkSoft
import com.example.ileauxtresors.ui.theme.Parchment
import com.example.ileauxtresors.ui.theme.SandDark

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

/**
 * Une carte Butin. Avec [onAcheter], bouton d'achat ; sans, juste le prix (aperçu depuis le
 * plateau), en or si le joueur pourra la payer.
 */
@Composable
fun ButinCardView(card: ButinCard, canBuy: Boolean, onAcheter: (() -> Unit)? = null) {
    PirateCard(color = Parchment) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f)) {
                Text(card.name, style = MaterialTheme.typography.titleLarge)
                Text(card.description, style = MaterialTheme.typography.bodyMedium, color = InkSoft)
            }
            if (onAcheter != null) {
                PirateButton(
                    "${card.cost} gourdes",
                    onClick = onAcheter,
                    enabled = canBuy,
                    compact = true,
                    modifier = Modifier.width(110.dp),
                )
            } else {
                Pill(
                    "${card.cost} gourdes",
                    background = if (canBuy) Gold else SandDark.copy(alpha = 0.4f),
                    color = Ink,
                )
            }
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

/**
 * Aperçu de la Taverne depuis le menu ☰ du plateau, pour décider de garder ou non ses gourdes.
 * Les gourdes déjà sur les dés comptent : elles seront gagnées en validant.
 */
@Composable
fun TaverneApercu(game: GameState, onClose: () -> Unit) {
    val gourds = game.player.gourds
    val onDice = if (game.currentIndex == GameState.PLAYER_INDEX) game.dice.count { it == DieFace.GOURDE } else 0
    Dialog(onDismissRequest = onClose) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Backdrop.TAVERNE.brush)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            CompositionLocalProvider(LocalContentColor provides Cream) {
                Text(
                    "Taverne du Butin",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    GourdBadge(gourds)
                    if (onDice > 0) {
                        Text(
                            "  + $onDice sur tes dés",
                            color = Gold,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
                Text(
                    "En or : les cartes que tu pourras payer après ce tour.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (game.tavernCards.isEmpty()) Text("La taverne n'a plus de cartes.", color = Cream)
            game.tavernCards.forEach { ButinCardView(it, canBuy = it.cost <= gourds + onDice) }
            PirateButton("Fermer", onClick = onClose, modifier = Modifier.fillMaxWidth())
        }
    }
}
