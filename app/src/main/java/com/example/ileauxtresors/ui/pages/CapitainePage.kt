package com.example.ileauxtresors.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ileauxtresors.model.Ship
import com.example.ileauxtresors.ui.components.Backdrop
import com.example.ileauxtresors.ui.components.PageScaffold
import com.example.ileauxtresors.ui.components.PirateButton
import com.example.ileauxtresors.ui.components.PirateCard
import com.example.ileauxtresors.ui.components.RoundIconButton
import com.example.ileauxtresors.ui.components.ShipMedallion
import com.example.ileauxtresors.ui.theme.Cream
import com.example.ileauxtresors.ui.theme.IleAuxTresorsTheme
import com.example.ileauxtresors.ui.theme.Ink
import com.example.ileauxtresors.ui.theme.InkSoft
import com.example.ileauxtresors.ui.theme.Sand
import com.example.ileauxtresors.ui.theme.SandDark

/** Page.CAPITAINE : choix du navire et du nom. Jouer → PLATEAU. */
@Composable
fun CapitainePage(
    onJouer: (name: String, ship: Ship) -> Unit,
    onRetour: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var ship by rememberSaveable { mutableStateOf(Ship.MAREE_BLEUE) }
    var name by rememberSaveable { mutableStateOf("") }

    PageScaffold(
        backdrop = Backdrop.PARCHEMIN,
        modifier = modifier,
        header = { RoundIconButton("←", "Retour", onClick = onRetour, background = Sand) },
        bottomBar = {
            Text(
                "Les 3 autres navires seront pilotés par des bots.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            PirateButton(
                "Hisser les voiles !",
                onClick = { onJouer(name.trim(), ship) },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            )
        },
    ) {
        Text("Choisissez votre navire", style = MaterialTheme.typography.headlineMedium)
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
        Text("NOM DU CAPITAINE", style = MaterialTheme.typography.labelLarge, color = InkSoft)
        NameField(name, onValueChange = { name = it })
    }
}

@Composable
private fun ShipCard(ship: Ship, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    PirateCard(
        modifier = modifier,
        selected = selected,
        badge = if (selected) "CHOISI" else null,
        onClick = onClick,
    ) {
        ShipMedallion(ship, Modifier.align(Alignment.CenterHorizontally))
        Text(
            ship.displayName,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        )
        Text(
            ship.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = InkSoft,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun NameField(value: String, onValueChange: (String) -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.titleMedium.copy(color = Ink),
        cursorBrush = SolidColor(Ink),
        modifier = Modifier.fillMaxWidth(),
        decorationBox = { field ->
            Box(
                Modifier
                    .background(Cream, shape)
                    .border(2.dp, SandDark, shape)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                if (value.isEmpty()) {
                    Text("Capitaine…", style = MaterialTheme.typography.titleMedium, color = InkSoft.copy(alpha = 0.6f))
                }
                field()
            }
        },
    )
}

@Preview(widthDp = 380, heightDp = 800)
@Composable
private fun CapitainePagePreview() {
    IleAuxTresorsTheme { CapitainePage(onJouer = { _, _ -> }, onRetour = {}) }
}
