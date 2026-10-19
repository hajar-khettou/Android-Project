package com.example.ileauxtresors.ui.pages

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.ileauxtresors.ui.theme.IleAuxTresorsTheme

/** Page.ATTAQUE : popup au-dessus du plateau. Rester / Fuir → PLATEAU. */
@Composable
fun AttaquePopup(
    onRester: () -> Unit,
    onFuir: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Fuir l'île ?") },
        // TODO : afficher qui attaque et combien de dégâts ont été reçus.
        text = { Text("Vous avez été touché sur l'île.") },
        confirmButton = { TextButton(onClick = onFuir) { Text("Fuir") } },
        dismissButton = { TextButton(onClick = onRester) { Text("Rester") } },
    )
}

@Preview
@Composable
private fun AttaquePopupPreview() {
    IleAuxTresorsTheme { AttaquePopup(onRester = {}, onFuir = {}) }
}
