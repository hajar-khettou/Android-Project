package com.example.ileauxtresors.ui.pages

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ileauxtresors.model.GameRules
import com.example.ileauxtresors.navigation.PageGroup
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
        Rule("Pièces", "Une série de pièces identiques rapporte une pièce en plus.")
        Rule("Canon", "1 dégât par canon.")
        Rule("Charpentier", "Répare 1 point de coque. Impossible sur l'île.")
        Rule("Gourde", "1 gourde d'eau, à dépenser à la Taverne du Butin.")
        Rule("L'île", "Un pirate touché sur l'île peut fuir : l'attaquant prend sa place.")
        OutlinedButton(onClick = onRetour, modifier = Modifier.fillMaxWidth()) { Text("Retour") }
    }
}

@Composable
private fun Rule(title: String, text: String) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    Text(text, style = MaterialTheme.typography.bodyMedium)
}

@Preview(showBackground = true)
@Composable
private fun ReglesPagePreview() {
    IleAuxTresorsTheme { ReglesPage(onRetour = {}) }
}
