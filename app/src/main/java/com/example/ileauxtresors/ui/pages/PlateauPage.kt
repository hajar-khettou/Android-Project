package com.example.ileauxtresors.ui.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ileauxtresors.model.Captain
import com.example.ileauxtresors.model.DieFace
import com.example.ileauxtresors.model.GameRules
import com.example.ileauxtresors.model.GameState
import com.example.ileauxtresors.model.Ship
import com.example.ileauxtresors.navigation.PageGroup
import com.example.ileauxtresors.ui.components.PageScaffold
import com.example.ileauxtresors.ui.theme.Gold
import com.example.ileauxtresors.ui.theme.IleAuxTresorsTheme

/**
 * Page.PLATEAU : bots, île, stats du joueur, 6 dés.
 * Valider les dés → TAVERNE. ATTAQUE et FIN sont déclenchées par le GameViewModel.
 */
@Composable
fun PlateauPage(
    game: GameState,
    onLancer: () -> Unit,
    onToggleKeep: (index: Int) -> Unit,
    onValider: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PageScaffold(title = "Au large · Tour ${game.turn}", group = PageGroup.PARTIE, modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            game.bots.forEach { CaptainCard(it, Modifier.weight(1f)) }
        }

        // TODO : illustration de l'île.
        val holder = game.islandHolder?.let { game.captains[it].name }
        Text(
            text = if (holder == null) "L'île est libre" else "$holder tient l'île",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        CaptainCard(game.player, Modifier.fillMaxWidth())

        // Pendant le tour d'un bot (popup ATTAQUE), on n'affiche pas ses dés.
        if (game.currentIndex != GameState.PLAYER_INDEX) {
            Text("${game.current.name} joue…", style = MaterialTheme.typography.labelLarge)
            return@PageScaffold
        }

        val rollNumber = GameRules.ROLLS_PER_TURN - game.rollsLeft
        Text("Lancer $rollNumber/${GameRules.ROLLS_PER_TURN}", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            game.dice.forEachIndexed { i, face ->
                DieView(face, kept = i in game.keptDice, onClick = { onToggleKeep(i) })
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onLancer,
                enabled = game.rollsLeft > 0,
                modifier = Modifier.weight(1f),
            ) { Text(if (game.dice.isEmpty()) "Lancer" else "Relancer") }
            Button(
                onClick = onValider,
                enabled = game.dice.isNotEmpty(),
                modifier = Modifier.weight(1f),
            ) { Text("Valider les dés") }
        }
    }
}

@Composable
private fun CaptainCard(captain: Captain, modifier: Modifier = Modifier) {
    OutlinedCard(modifier) {
        Column(Modifier.padding(8.dp)) {
            Text(captain.name, style = MaterialTheme.typography.titleSmall)
            Text("Or ${captain.gold}/${GameRules.GOLD_TO_WIN}", style = MaterialTheme.typography.bodySmall)
            Text("Coque ${captain.hull}/${GameRules.MAX_HULL}", style = MaterialTheme.typography.bodySmall)
            if (!captain.isBot) Text("Gourdes ${captain.gourds}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun DieView(face: DieFace, kept: Boolean, onClick: () -> Unit) {
    OutlinedCard(
        onClick = onClick,
        border = if (kept) BorderStroke(3.dp, Gold) else BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        // TODO : icônes des faces à la place du texte.
        Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            Text(face.label.take(5), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlateauPagePreview() {
    val game = GameState.newGame("Capitaine Amina", Ship.MAREE_BLEUE).copy(
        islandHolder = 1,
        dice = listOf(DieFace.CANON, DieFace.CANON, DieFace.OR, DieFace.CUIVRE, DieFace.GOURDE, DieFace.CHARPENTIER),
        keptDice = setOf(0, 1),
        rollsLeft = 1,
    )
    IleAuxTresorsTheme { PlateauPage(game, onLancer = {}, onToggleKeep = {}, onValider = {}) }
}
