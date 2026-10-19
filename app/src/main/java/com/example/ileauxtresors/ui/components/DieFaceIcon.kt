package com.example.ileauxtresors.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ileauxtresors.R
import com.example.ileauxtresors.model.DieFace

/** Dessin d'une face de dé : une pièce numérotée, ou l'icône canon / gourde / charpentier. */
@Composable
fun DieFaceIcon(face: DieFace, modifier: Modifier = Modifier, size: Dp = 32.dp) {
    val description = faceName(face)
    if (face.coinValue > 0) {
        val (fill, rim) = coinColors(face)
        Box(
            modifier = modifier
                .size(size)
                .background(fill, CircleShape)
                .border(2.dp, rim, CircleShape)
                .semantics { contentDescription = description },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = face.coinValue.toString(),
                color = Color(0xFF3E2A10),
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.5f).sp,
            )
        }
    } else {
        Image(
            painter = painterResource(faceDrawable(face)),
            contentDescription = description,
            modifier = modifier.size(size),
        )
    }
}

fun faceName(face: DieFace): String = when (face) {
    DieFace.CUIVRE -> "Cuivre (1)"
    DieFace.ARGENT -> "Argent (2)"
    DieFace.OR -> "Or (3)"
    DieFace.CANON -> "Canon"
    DieFace.CHARPENTIER -> "Charpentier"
    DieFace.GOURDE -> "Gourde"
}

private fun faceDrawable(face: DieFace): Int = when (face) {
    DieFace.CANON -> R.drawable.ic_die_canon
    DieFace.CHARPENTIER -> R.drawable.ic_die_charpentier
    DieFace.GOURDE -> R.drawable.ic_die_gourde
    else -> error("$face est une pièce, pas une icône")
}

/** Couleur de la pièce et de son bord. */
private fun coinColors(face: DieFace): Pair<Color, Color> = when (face) {
    DieFace.CUIVRE -> Color(0xFFD08A4E) to Color(0xFF9A5A2A)
    DieFace.ARGENT -> Color(0xFFD6DCE0) to Color(0xFF8E9AA2)
    else -> Color(0xFFF2C14E) to Color(0xFFB88A1C)
}

@Preview(showBackground = true)
@Composable
private fun DieFaceIconPreview() {
    Row(Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DieFace.entries.forEach { DieFaceIcon(it, size = 40.dp) }
    }
}
