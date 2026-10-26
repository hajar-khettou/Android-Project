package com.example.ileauxtresors.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ileauxtresors.model.Captain
import com.example.ileauxtresors.model.DieFace
import com.example.ileauxtresors.model.GameRules
import com.example.ileauxtresors.model.GameState
import com.example.ileauxtresors.model.Ship
import com.example.ileauxtresors.ui.components.Backdrop
import com.example.ileauxtresors.ui.components.CaptainAvatar
import com.example.ileauxtresors.ui.components.CoinIcon
import com.example.ileauxtresors.ui.components.DieFaceIcon
import com.example.ileauxtresors.ui.components.GoldBar
import com.example.ileauxtresors.ui.components.GourdBadge
import com.example.ileauxtresors.ui.components.HullSegments
import com.example.ileauxtresors.ui.components.IslandScene
import com.example.ileauxtresors.ui.components.PageHeader
import com.example.ileauxtresors.ui.components.PageScaffold
import com.example.ileauxtresors.ui.components.Pill
import com.example.ileauxtresors.ui.components.PirateButton
import com.example.ileauxtresors.ui.components.PirateButtonStyle
import com.example.ileauxtresors.ui.components.PirateCard
import com.example.ileauxtresors.ui.components.RoundIconButton
import com.example.ileauxtresors.ui.components.ShipIcon
import com.example.ileauxtresors.ui.theme.Cream
import com.example.ileauxtresors.ui.theme.Gold
import com.example.ileauxtresors.ui.theme.IleAuxTresorsTheme
import com.example.ileauxtresors.ui.theme.InkSoft
import com.example.ileauxtresors.ui.theme.Night
import com.example.ileauxtresors.ui.theme.Parchment
import com.example.ileauxtresors.ui.theme.SandDark
import com.example.ileauxtresors.ui.theme.Sea
import com.example.ileauxtresors.ui.theme.Wood
import com.example.ileauxtresors.ui.theme.WoodDark

/**
 * Page.PLATEAU : bots, île, stats du joueur, 6 dés.
 * Valider les dés → TAVERNE. ATTAQUE et FIN sont déclenchées par le GameViewModel.
 * Pendant le tour d'un bot, on voit qui joue et ses dés, sans pouvoir agir.
 */
@Composable
fun PlateauPage(
    game: GameState,
    onLancer: () -> Unit,
    onToggleKeep: (index: Int) -> Unit,
    onValider: () -> Unit,
    onRegles: () -> Unit,
    onQuitter: () -> Unit,
    modifier: Modifier = Modifier,
    botLog: List<String> = emptyList(),
) {
    val playerTurn = game.currentIndex == GameState.PLAYER_INDEX
    PageScaffold(
        backdrop = Backdrop.NUIT,
        modifier = modifier,
        header = {
            PageHeader(
                title = if (game.islandHolder == GameState.PLAYER_INDEX) "Sur l'île" else "Au large",
                start = { Pill("Tour ${game.turn}") },
                end = { GameMenu(enabled = playerTurn, onRegles = onRegles, onQuitter = onQuitter) },
            )
        },
        bottomBar = {
            if (playerTurn) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PirateButton(
                        if (game.dice.isEmpty()) "Lancer" else "↻ Relancer",
                        onClick = onLancer,
                        enabled = game.rollsLeft > 0,
                        style = PirateButtonStyle.BOIS,
                        modifier = Modifier.weight(1f),
                    )
                    PirateButton(
                        "Valider les dés",
                        onClick = onValider,
                        enabled = game.dice.isNotEmpty(),
                        modifier = Modifier.weight(1.4f),
                    )
                }
            }
        },
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            game.captains.forEachIndexed { i, c ->
                if (i != GameState.PLAYER_INDEX) {
                    BotCard(
                        captain = c,
                        onIsland = game.islandHolder == i,
                        playing = game.currentIndex == i,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        Island(game)
        if (botLog.isNotEmpty()) BotLogCard(botLog)
        PlayerCard(game.player)
        DiceTray(game, playerTurn, onToggleKeep)
    }
}

/** Le menu ☰ : revoir les règles ou quitter la partie. Désactivé pendant le tour des bots. */
@Composable
private fun GameMenu(enabled: Boolean, onRegles: () -> Unit, onQuitter: () -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        RoundIconButton("☰", "Menu", onClick = { open = true }, enabled = enabled)
        DropdownMenu(expanded = open, onDismissRequest = { open = false }, containerColor = Parchment) {
            DropdownMenuItem(text = { Text("Règles") }, onClick = { open = false; onRegles() })
            DropdownMenuItem(text = { Text("Quitter la partie") }, onClick = { open = false; onQuitter() })
        }
    }
}

@Composable
private fun BotCard(captain: Captain, onIsland: Boolean, playing: Boolean, modifier: Modifier = Modifier) {
    PirateCard(
        modifier = modifier.alpha(if (captain.isSunk) 0.4f else 1f),
        color = Color.White.copy(alpha = 0.08f),
        contentColor = Cream,
        borderColor = Color.White.copy(alpha = 0.15f),
        selected = playing,
        badge = when {
            captain.isSunk -> "COULÉ"
            onIsland -> "SUR L'ÎLE"
            else -> null
        },
        badgeColor = if (captain.isSunk) SandDark else Sea,
        badgeAlignment = Alignment.TopCenter,
    ) {
        CaptainAvatar(captain, Modifier.align(Alignment.CenterHorizontally))
        Text(
            captain.name,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CoinIcon(size = 12.dp)
            Text(" ${captain.gold}", style = MaterialTheme.typography.labelMedium, color = Gold)
            Spacer(Modifier.width(8.dp))
            ShipIcon(Cream, Modifier.size(14.dp), hullColor = Cream)
            Text(" ${captain.hull}", style = MaterialTheme.typography.labelMedium)
        }
        GoldBar(captain.gold, Modifier.padding(top = 6.dp), height = 5.dp, track = Color.White.copy(alpha = 0.15f))
    }
}

/** L'île, le navire qui y est amarré et qui la tient. */
@Composable
private fun Island(game: GameState) {
    val holder = game.islandHolder?.let { game.captains[it] }
    Box(contentAlignment = Alignment.BottomCenter) {
        IslandScene(Modifier.padding(bottom = 12.dp), dockedShip = holder?.ship)
        Pill(
            if (holder == null) "L'île est libre" else "♛ ${holder.name} tient l'île",
            background = Night.copy(alpha = 0.85f),
        )
    }
}

/** Ce que les bots ont fait depuis ton dernier tour. Replié par défaut pour laisser voir les dés. */
@Composable
private fun BotLogCard(lines: List<String>) {
    var open by rememberSaveable { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .clickable(role = Role.Button, onClickLabel = if (open) "Replier" else "Déplier") { open = !open }
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text("Pendant ce temps… " + if (open) "▴" else "▾", style = MaterialTheme.typography.labelLarge)
        // Replié : seulement la dernière action, sur une ligne.
        if (open) {
            lines.forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
        } else {
            Text(lines.last(), style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun PlayerCard(player: Captain) {
    PirateCard(color = Parchment) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CaptainAvatar(player, size = 44.dp)
            Text(
                player.name,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            )
            GourdBadge(player.gourds)
        }
        Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            CoinIcon()
            Text(" Pièces d'or", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
            Text("${player.gold} / ${GameRules.GOLD_TO_WIN}", style = MaterialTheme.typography.labelLarge)
        }
        GoldBar(player.gold, Modifier.padding(vertical = 6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            ShipIcon(InkSoft, Modifier.size(16.dp))
            Text(" Coque", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
            HullSegments(player.hull)
        }
    }
}

/** Le bac à dés en bois. Au tour d'un bot, on y voit ses dés. */
@Composable
private fun DiceTray(game: GameState, playerTurn: Boolean, onToggleKeep: (Int) -> Unit) {
    val rollNumber = GameRules.ROLLS_PER_TURN - game.rollsLeft
    val title = when {
        !playerTurn -> "${game.current.name} joue…"
        game.dice.isEmpty() -> "Lancez les dés !"
        game.rollsLeft == 0 -> "Lancer $rollNumber / ${GameRules.ROLLS_PER_TURN} · validez vos dés"
        else -> "Lancer $rollNumber / ${GameRules.ROLLS_PER_TURN} · touchez un dé pour le garder"
    }
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Wood)
            .border(2.dp, WoodDark, RoundedCornerShape(20.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(title, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(GameRules.DICE_COUNT) { i ->
                DieView(
                    face = game.dice.getOrNull(i),
                    kept = playerTurn && i in game.keptDice,
                    onClick = if (playerTurn && game.dice.isNotEmpty() && game.rollsLeft > 0) {
                        { onToggleKeep(i) }
                    } else {
                        null
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/** Un dé crème en relief. Gardé : contour doré et « GARDÉ » dessous. Sans face : dé pas encore lancé. */
@Composable
private fun DieView(face: DieFace?, kept: Boolean, onClick: (() -> Unit)?, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(10.dp)
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .alpha(if (face == null) 0.35f else 1f)
                .clip(shape)
                .background(SandDark)
                .then(if (onClick != null) Modifier.clickable(role = Role.Checkbox, onClick = onClick) else Modifier)
                .padding(bottom = 4.dp)
                .clip(shape)
                .background(Cream)
                .border(if (kept) 3.dp else 0.dp, if (kept) Gold else Color.Transparent, shape),
            contentAlignment = Alignment.Center,
        ) {
            if (face != null) DieFaceIcon(face, size = 28.dp)
        }
        Text(
            if (kept) "GARDÉ" else " ",
            color = Gold,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

@Preview(widthDp = 380, heightDp = 900)
@Composable
private fun PlateauPagePreview() {
    val game = GameState.newGame("Capitaine Amina", Ship.MAREE_BLEUE).copy(
        islandHolder = 1,
        dice = listOf(DieFace.CANON, DieFace.CANON, DieFace.OR, DieFace.CUIVRE, DieFace.GOURDE, DieFace.CHARPENTIER),
        keptDice = setOf(0, 1),
        rollsLeft = 1,
    )
    val botLog = listOf(
        "Barbe-Grise : +3 or, s'amarre à l'île",
        "Anne la Rouge : touche Barbe-Grise (−2), +1 gourde",
        "Jade Sabre : +2 or, achète « Tonneau de rhum »",
    )
    IleAuxTresorsTheme {
        PlateauPage(game, onLancer = {}, onToggleKeep = {}, onValider = {}, onRegles = {}, onQuitter = {}, botLog = botLog)
    }
}
