package com.example.ileauxtresors.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ileauxtresors.model.Ship
import com.example.ileauxtresors.ui.theme.Cream
import com.example.ileauxtresors.ui.theme.Gold
import com.example.ileauxtresors.ui.theme.GoldDark
import com.example.ileauxtresors.ui.theme.Night
import com.example.ileauxtresors.ui.theme.NightLight
import com.example.ileauxtresors.ui.theme.Palm
import com.example.ileauxtresors.ui.theme.Sand
import com.example.ileauxtresors.ui.theme.SandDark
import com.example.ileauxtresors.ui.theme.Sea
import com.example.ileauxtresors.ui.theme.SeaDark
import com.example.ileauxtresors.ui.theme.color

// Les dessins des maquettes, faits avec des formes simples (ovales, arcs, chemins).
// Toutes les coordonnées sont en fraction de la taille du dessin.

private val HullBrown = Color(0xFF4A2A14)
private val TrunkBrown = Color(0xFF7A4A22)
private val ChestBrown = Color(0xFF6B3E1E)

/** Un navire : coque, mât, voile et pavillon à la couleur du capitaine. */
@Composable
fun ShipIcon(flagColor: Color, modifier: Modifier = Modifier, sailColor: Color = Cream, hullColor: Color = HullBrown) {
    Canvas(modifier) { drawShip(Offset.Zero, size, flagColor, sailColor, hullColor) }
}

/** Le navire dans un médaillon crème (écran du capitaine). */
@Composable
fun ShipMedallion(ship: Ship, modifier: Modifier = Modifier, size: Dp = 76.dp) {
    Box(
        modifier
            .size(size)
            .clip(CircleShape)
            .background(Cream),
        contentAlignment = Alignment.Center,
    ) {
        ShipIcon(ship.color(), Modifier.size(size * 0.8f), sailColor = Sand)
    }
}

/** L'île : mer, sable, palmier, coffre. [dockedShip] est le navire amarré, s'il y en a un. */
@Composable
fun IslandScene(modifier: Modifier = Modifier, dockedShip: Ship? = null) {
    Canvas(
        modifier
            .fillMaxWidth()
            .aspectRatio(2.2f),
    ) {
        val w = size.width
        val h = size.height

        drawOval(SeaDark, Offset(0.02f * w, 0.52f * h), Size(0.96f * w, 0.42f * h))
        drawOval(Sea, Offset(0.12f * w, 0.58f * h), Size(0.76f * w, 0.26f * h))
        drawArc(Sand, 180f, 180f, true, Offset(0.18f * w, 0.42f * h), Size(0.64f * w, 0.56f * h))
        drawArc(SandDark.copy(alpha = 0.35f), 200f, 60f, true, Offset(0.24f * w, 0.5f * h), Size(0.4f * w, 0.4f * h))

        // Palmier
        val top = Offset(0.43f * w, 0.12f * h)
        val trunk = Path().apply {
            moveTo(0.39f * w, 0.66f * h)
            quadraticTo(0.37f * w, 0.36f * h, top.x, top.y)
        }
        drawPath(trunk, TrunkBrown, style = Stroke(width = 0.018f * w, cap = StrokeCap.Round))
        listOf(
            Offset(-0.15f * w, 0.10f * h),
            Offset(-0.08f * w, -0.06f * h),
            Offset(0.09f * w, -0.05f * h),
            Offset(0.16f * w, 0.12f * h),
        ).forEach { d -> drawLeaf(top, top + d, 0.07f * h) }

        // Coffre et pièces
        drawRoundRect(ChestBrown, Offset(0.5f * w, 0.5f * h), Size(0.075f * w, 0.1f * h), CornerRadius(0.008f * w))
        drawRect(Color(0xFF4A2A14), Offset(0.5f * w, 0.535f * h), Size(0.075f * w, 0.012f * h))
        drawRect(Gold, Offset(0.531f * w, 0.53f * h), Size(0.014f * w, 0.035f * h))
        drawCircle(Gold, 0.012f * w, Offset(0.6f * w, 0.6f * h))
        drawCircle(GoldDark, 0.012f * w, Offset(0.618f * w, 0.585f * h))

        if (dockedShip != null) {
            drawShip(Offset(0.62f * w, 0.26f * h), Size(0.17f * w, 0.42f * h), dockedShip.color())
        }

        drawWave(Offset(0.03f * w, 0.18f * h), 0.08f * w)
        drawWave(Offset(0.86f * w, 0.88f * h), 0.09f * w)
    }
}

/**
 * Le ciel de nuit de l'accueil : étoiles, lune, et la mer en bas avec un navire.
 * [starsOnly] ne garde que les étoiles, pour les pages où le contenu occupe tout l'écran.
 */
@Composable
fun NightSky(modifier: Modifier = Modifier, starsOnly: Boolean = false) {
    Canvas(modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        Stars.forEach { (x, y) -> drawCircle(Cream.copy(alpha = 0.55f), 0.005f * w, Offset(x * w, y * h)) }
        if (starsOnly) return@Canvas

        val moon = Offset(0.8f * w, 0.12f * h)
        drawCircle(NightLight, 0.12f * w, moon + Offset(0.03f * w, -0.01f * h))
        drawCircle(Cream, 0.09f * w, moon)
        drawCircle(NightLight, 0.08f * w, moon + Offset(0.035f * w, -0.012f * h))

        val sea = Path().apply {
            moveTo(0f, 0.74f * h)
            cubicTo(0.3f * w, 0.70f * h, 0.6f * w, 0.78f * h, w, 0.72f * h)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(sea, NightLight)
        drawShip(Offset(0.68f * w, 0.62f * h), Size(0.2f * w, 0.12f * h), Gold)
        drawWave(Offset(0.12f * w, 0.66f * h), 0.08f * w)
    }
}

private val Stars = listOf(
    0.08f to 0.08f, 0.22f to 0.15f, 0.4f to 0.05f, 0.55f to 0.12f, 0.12f to 0.3f,
    0.9f to 0.3f, 0.65f to 0.04f, 0.3f to 0.25f, 0.95f to 0.08f, 0.5f to 0.32f,
)

private fun DrawScope.drawShip(origin: Offset, size: Size, flagColor: Color, sailColor: Color = Cream, hullColor: Color = HullBrown) {
    val w = size.width
    val h = size.height
    fun p(x: Float, y: Float) = Offset(origin.x + x * w, origin.y + y * h)

    drawOval(sailColor, p(0.3f, 0.26f), Size(0.4f * w, 0.38f * h))
    drawOval(Color(0xFFD9CFC0), p(0.5f, 0.3f), Size(0.16f * w, 0.3f * h))
    drawLine(hullColor, p(0.5f, 0.1f), p(0.5f, 0.72f), strokeWidth = 0.05f * w)
    val flag = Path().apply {
        moveTo(p(0.52f, 0.08f).x, p(0.52f, 0.08f).y)
        lineTo(p(0.76f, 0.13f).x, p(0.76f, 0.13f).y)
        lineTo(p(0.52f, 0.2f).x, p(0.52f, 0.2f).y)
        close()
    }
    drawPath(flag, flagColor)
    val hull = Path().apply {
        moveTo(p(0.08f, 0.68f).x, p(0.08f, 0.68f).y)
        lineTo(p(0.92f, 0.68f).x, p(0.92f, 0.68f).y)
        lineTo(p(0.76f, 0.9f).x, p(0.76f, 0.9f).y)
        lineTo(p(0.24f, 0.9f).x, p(0.24f, 0.9f).y)
        close()
    }
    drawPath(hull, hullColor)
}

/** Une feuille de palmier, de [from] jusqu'à sa pointe [tip]. */
private fun DrawScope.drawLeaf(from: Offset, tip: Offset, bulge: Float) {
    val mid = (from + tip) / 2f
    val leaf = Path().apply {
        moveTo(from.x, from.y)
        quadraticTo(mid.x, mid.y - bulge, tip.x, tip.y)
        quadraticTo(mid.x, mid.y + bulge * 0.2f, from.x, from.y)
        close()
    }
    drawPath(leaf, Palm)
}

/** Une petite vague « ~ ». */
private fun DrawScope.drawWave(start: Offset, length: Float) {
    val bump = length / 4f
    val wave = Path().apply {
        moveTo(start.x, start.y)
        quadraticTo(start.x + bump, start.y - bump * 0.6f, start.x + 2 * bump, start.y)
        quadraticTo(start.x + 3 * bump, start.y + bump * 0.6f, start.x + length, start.y)
    }
    drawPath(wave, Cream.copy(alpha = 0.35f), style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
private fun IllustrationsPreview() {
    Box(Modifier.background(Night)) {
        NightSky()
        IslandScene(Modifier.align(Alignment.Center).padding(16.dp), dockedShip = Ship.MAREE_BLEUE)
    }
}
