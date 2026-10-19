package com.example.ileauxtresors.ui.pages

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ileauxtresors.navigation.PageGroup
import com.example.ileauxtresors.ui.components.PageScaffold
import com.example.ileauxtresors.ui.theme.IleAuxTresorsTheme

/** Page.ACCUEIL : Jouer → CAPITAINE · Règles → REGLES. */
@Composable
fun AccueilPage(
    onJouer: () -> Unit,
    onRegles: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PageScaffold(title = "Île aux Trésors", group = PageGroup.MENU, modifier = modifier) {
        Text("King of Tokyo · édition pirate", style = MaterialTheme.typography.labelLarge)
        Text("4 capitaines. 1 île. 20 pièces d'or.", style = MaterialTheme.typography.headlineSmall)
        // TODO : illustration (île, coffre, navire) de la maquette.
        Button(onClick = onJouer, modifier = Modifier.fillMaxWidth()) { Text("Nouvelle partie") }
        OutlinedButton(onClick = onRegles, modifier = Modifier.fillMaxWidth()) { Text("Règles") }
    }
}

@Preview(showBackground = true)
@Composable
private fun AccueilPagePreview() {
    IleAuxTresorsTheme { AccueilPage(onJouer = {}, onRegles = {}) }
}
