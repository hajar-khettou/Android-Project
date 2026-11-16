package com.example.ileauxtresors.ui.pages

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ileauxtresors.ui.components.Backdrop
import com.example.ileauxtresors.ui.components.IslandScene
import com.example.ileauxtresors.ui.components.NightSky
import com.example.ileauxtresors.ui.components.PageScaffold
import com.example.ileauxtresors.ui.components.Pill
import com.example.ileauxtresors.ui.components.PirateButton
import com.example.ileauxtresors.ui.components.PirateButtonStyle
import com.example.ileauxtresors.ui.theme.Gold
import com.example.ileauxtresors.ui.theme.IleAuxTresorsTheme
import com.example.ileauxtresors.ui.theme.WoodDark

/** Page.ACCUEIL : Jouer → CAPITAINE · Règles → REGLES. */
@Composable
fun AccueilPage(
    onJouer: () -> Unit,
    onRegles: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PageScaffold(
        backdrop = Backdrop.NUIT,
        modifier = modifier,
        scenery = { NightSky() },
        bottomBar = {
            PirateButton("⛵ Nouvelle partie", onClick = onJouer, modifier = Modifier.fillMaxWidth())
            PirateButton(
                "Règles",
                onClick = onRegles,
                style = PirateButtonStyle.CONTOUR,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    ) {
        Spacer(Modifier.height(48.dp))
        Pill(
            "⚓ King of Tokyo · édition pirate",
            color = Gold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Text(
            text = "Île aux\nTrésors",
            color = Gold,
            style = MaterialTheme.typography.displayLarge.copy(
                shadow = Shadow(WoodDark, offset = Offset(0f, 10f), blurRadius = 4f),
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            "4 capitaines. 1 île. 20 pièces d'or.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(24.dp))
        IslandScene()
    }
}

@Preview(widthDp = 380, heightDp = 800)
@Composable
private fun AccueilPagePreview() {
    IleAuxTresorsTheme { AccueilPage(onJouer = {}, onRegles = {}) }
}
