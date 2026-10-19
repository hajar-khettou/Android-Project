package com.example.ileauxtresors.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ileauxtresors.model.DieFace
import com.example.ileauxtresors.model.GameRules
import com.example.ileauxtresors.navigation.PageGroup
import com.example.ileauxtresors.ui.components.DieFaceIcon
import com.example.ileauxtresors.ui.components.PageScaffold
import com.example.ileauxtresors.ui.theme.IleAuxTresorsTheme

/** Page.REGLES : Retour → ACCUEIL. */
@Composable
fun ReglesPage(
    onRetour: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PageScaffold(title = "Règles", group = PageGroup.MENU, modifier = modifier) {
        // TODO : texte complet et mis en forme des règles.
        Rule("But", "Atteindre ${GameRules.GOLD_TO_WIN} pièces d'or, ou être le dernier navire à flot.")
        Rule(
            "Ton tour",
            "Si tu commences sur l'île : +${GameRules.ISLAND_START_BONUS} or. " +
                "Lance ${GameRules.DICE_COUNT} dés, relance jusqu'à 2 fois, puis valide. " +
                "Si l'île est libre, amarre-toi : +${GameRules.ISLAND_DOCK_BONUS} or.",
        )
        Rule(
            "Pièces",
            "3 faces identiques rapportent leur valeur (cuivre 1, argent 2, or 3), " +
                "+1 or par face identique en plus.",
            faces = listOf(DieFace.CUIVRE, DieFace.ARGENT, DieFace.OR),
        )
        Rule(
            "Canon",
            "1 dégât par canon. Depuis l'île, touche tous les autres ; au large, touche celui sur l'île.",
            faces = listOf(DieFace.CANON),
        )
        Rule("Charpentier", "Répare 1 point de coque. Impossible sur l'île.", faces = listOf(DieFace.CHARPENTIER))
        Rule("Gourde", "1 gourde d'eau, à dépenser à la Taverne du Butin.", faces = listOf(DieFace.GOURDE))
        Rule("L'île", "Un pirate touché sur l'île peut fuir : l'attaquant prend sa place.")
        OutlinedButton(onClick = onRetour, modifier = Modifier.fillMaxWidth()) { Text("Retour") }
    }
}

@Composable
private fun Rule(title: String, text: String, faces: List<DieFace> = emptyList()) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        faces.forEach { DieFaceIcon(it, size = 28.dp) }
        Text(title, style = MaterialTheme.typography.titleMedium)
    }
    Text(text, style = MaterialTheme.typography.bodyMedium)
}

@Preview(showBackground = true)
@Composable
private fun ReglesPagePreview() {
    IleAuxTresorsTheme { ReglesPage(onRetour = {}) }
}
