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
import com.example.ileauxtresors.ui.theme.MenuBlue

/** Page.REGLES : Retour → ACCUEIL. */
@Composable
fun ReglesPage(
    onRetour: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PageScaffold(title = "Règles", group = PageGroup.MENU, modifier = modifier) {
        Section("Le jeu")
        Rule(
            "But",
            "${GameRules.PLAYER_COUNT} capitaines, 1 île. Le premier à ${GameRules.GOLD_TO_WIN} pièces d'or gagne. " +
                "Si un navire coule (coque à 0), il est éliminé : le dernier à flot gagne aussi.",
        )
        Rule(
            "Ton navire",
            "Tu commences avec ${GameRules.MAX_HULL} points de coque, 0 or et 0 gourde.",
        )

        Section("Ton tour")
        Rule(
            "1. Sur l'île ?",
            "Si tu commences ton tour sur l'île : +${GameRules.ISLAND_START_BONUS} or.",
        )
        Rule(
            "2. Les dés",
            "Lance les ${GameRules.DICE_COUNT} dés. Touche un dé pour le garder, puis relance les autres, " +
                "jusqu'à ${GameRules.ROLLS_PER_TURN - 1} fois. Valide pour appliquer le résultat.",
        )
        Rule(
            "3. L'amarrage",
            "Si l'île est libre après tes dés, tu t'y amarres : +${GameRules.ISLAND_DOCK_BONUS} or.",
        )
        Rule(
            "4. La Taverne",
            "Dépense tes gourdes pour acheter des cartes Butin, puis termine ton tour.",
        )

        Section("Les faces des dés")
        Rule(
            "Pièces",
            "3 faces identiques rapportent leur valeur (cuivre 1, argent 2, or 3), " +
                "+1 or par face identique en plus. Ex. : 4 argent = 3 or.",
            faces = listOf(DieFace.CUIVRE, DieFace.ARGENT, DieFace.OR),
        )
        Rule(
            "Canon",
            "1 dégât par canon. Depuis l'île, touche tous les autres ; au large, touche celui sur l'île.",
            faces = listOf(DieFace.CANON),
        )
        Rule(
            "Charpentier",
            "Répare 1 point de coque (max ${GameRules.MAX_HULL}). Impossible sur l'île.",
            faces = listOf(DieFace.CHARPENTIER),
        )
        Rule("Gourde", "1 gourde d'eau, à dépenser à la Taverne du Butin.", faces = listOf(DieFace.GOURDE))

        Section("L'île")
        Rule(
            "Tenir l'île",
            "Sur l'île, tes canons touchent tout le monde et tu gagnes de l'or à chaque tour, " +
                "mais tu ne peux pas te réparer.",
        )
        Rule(
            "Fuir",
            "Touché sur l'île, tu choisis : rester, ou fuir au large. Si tu fuis, ton attaquant prend ta place.",
        )

        Section("La Taverne du Butin")
        Rule(
            "Cartes Butin",
            "${GameRules.TAVERN_VISIBLE_CARDS} cartes sont visibles. Chacune coûte des gourdes et agit tout de suite : " +
                "or, réparation, bordée sur les autres navires ou pillage.",
        )
        Rule(
            "Balayer",
            "Pour ${GameRules.SWEEP_COST} gourdes, remplace les ${GameRules.TAVERN_VISIBLE_CARDS} cartes visibles.",
        )
        OutlinedButton(onClick = onRetour, modifier = Modifier.fillMaxWidth()) { Text("Retour") }
    }
}

@Composable
private fun Section(title: String) {
    Text(title, style = MaterialTheme.typography.titleLarge, color = MenuBlue)
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
