package com.example.ileauxtresors.ui.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ileauxtresors.model.Ship
import com.example.ileauxtresors.navigation.PageGroup
import com.example.ileauxtresors.ui.components.PageScaffold
import com.example.ileauxtresors.ui.theme.Gold
import com.example.ileauxtresors.ui.theme.IleAuxTresorsTheme

/** Page.CAPITAINE : choix du navire et du nom. Jouer → PLATEAU. */
@Composable
fun CapitainePage(
    onJouer: (name: String, ship: Ship) -> Unit,
    modifier: Modifier = Modifier,
) {
    var ship by rememberSaveable { mutableStateOf(Ship.MAREE_BLEUE) }
    var name by rememberSaveable { mutableStateOf("") }

    PageScaffold(title = "Choisissez votre navire", group = PageGroup.MENU, modifier = modifier) {
        Ship.entries.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { s ->
                    ShipCard(
                        ship = s,
                        selected = s == ship,
                        onClick = { ship = s },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nom du capitaine") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Text("Les 3 autres navires sont joués par des bots.", style = MaterialTheme.typography.bodySmall)
        Button(
            onClick = { onJouer(name.trim(), ship) },
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Hisser les voiles !") }
    }
}

@Composable
private fun ShipCard(ship: Ship, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedCard(
        onClick = onClick,
        border = if (selected) BorderStroke(3.dp, Gold) else BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier,
    ) {
        Column(Modifier.padding(12.dp)) {
            // TODO : illustration du navire.
            Text(ship.displayName, style = MaterialTheme.typography.titleSmall)
            Text(ship.subtitle, style = MaterialTheme.typography.bodySmall)
            if (selected) Text("CHOISI", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CapitainePagePreview() {
    IleAuxTresorsTheme { CapitainePage(onJouer = { _, _ -> }) }
}
