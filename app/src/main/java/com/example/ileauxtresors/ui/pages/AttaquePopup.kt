package com.example.ileauxtresors.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ileauxtresors.model.DieFace
import com.example.ileauxtresors.ui.components.DieFaceIcon
import com.example.ileauxtresors.ui.components.PirateButton
import com.example.ileauxtresors.ui.components.PirateButtonStyle
import com.example.ileauxtresors.ui.components.PirateCard
import com.example.ileauxtresors.ui.theme.IleAuxTresorsTheme
import com.example.ileauxtresors.ui.theme.Parchment

/** Page.ATTAQUE : popup au-dessus du plateau grisé. Rester / Fuir → PLATEAU. */
@Composable
fun AttaquePopup(
    attackerName: String,
    damage: Int,
    onRester: () -> Unit,
    onFuir: () -> Unit,
) {
    // Il faut choisir : ni le retour ni un clic à côté ne ferment la popup.
    PiratePopup(
        title = "Fuir l'île ?",
        message = "$attackerName vous a touché : −$damage coque.\nSi vous fuyez, $attackerName prend l'île.",
        dismissText = "Rester",
        confirmText = "Fuir",
        onDismiss = onRester,
        onConfirm = onFuir,
        dismissible = false,
    ) {
        DieFaceIcon(DieFace.CANON, size = 48.dp)
    }
}

/** Popup « Quitter la partie ? » (menu ☰ ou bouton retour pendant une partie). */
@Composable
fun QuitterPopup(onRester: () -> Unit, onQuitter: () -> Unit) {
    PiratePopup(
        title = "Quitter la partie ?",
        message = "La partie en cours sera perdue.",
        dismissText = "Rester",
        confirmText = "Quitter",
        onDismiss = onRester,
        onConfirm = onQuitter,
    )
}

/** Popup en parchemin avec deux boutons : bois pour [dismissText], or pour [confirmText]. */
@Composable
private fun PiratePopup(
    title: String,
    message: String,
    dismissText: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    dismissible: Boolean = true,
    icon: @Composable () -> Unit = {},
) {
    Dialog(
        onDismissRequest = { if (dismissible) onDismiss() },
        properties = DialogProperties(dismissOnBackPress = dismissible, dismissOnClickOutside = dismissible),
    ) {
        PirateCard(color = Parchment) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) { icon() }
            Text(
                title,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                PirateButton(dismissText, onClick = onDismiss, style = PirateButtonStyle.BOIS, modifier = Modifier.weight(1f))
                PirateButton(confirmText, onClick = onConfirm, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Preview
@Composable
private fun AttaquePopupPreview() {
    IleAuxTresorsTheme { AttaquePopup(attackerName = "Barbe-Grise", damage = 2, onRester = {}, onFuir = {}) }
}
