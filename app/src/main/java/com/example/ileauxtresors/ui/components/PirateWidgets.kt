package com.example.ileauxtresors.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ileauxtresors.R
import com.example.ileauxtresors.model.Captain
import com.example.ileauxtresors.model.GameRules
import com.example.ileauxtresors.ui.theme.Cream
import com.example.ileauxtresors.ui.theme.Gold
import com.example.ileauxtresors.ui.theme.GoldDark
import com.example.ileauxtresors.ui.theme.GourdBlue
import com.example.ileauxtresors.ui.theme.Ink
import com.example.ileauxtresors.ui.theme.Pirata
import com.example.ileauxtresors.ui.theme.Sand
import com.example.ileauxtresors.ui.theme.SandDark
import com.example.ileauxtresors.ui.theme.Wood
import com.example.ileauxtresors.ui.theme.WoodDark
import com.example.ileauxtresors.ui.theme.WoodLight
import com.example.ileauxtresors.ui.theme.color

/** Les trois boutons des maquettes : or (action principale), bois (Relancer), contour. */
enum class PirateButtonStyle(val face: Color, val lip: Color, val text: Color?) {
    OR(Gold, GoldDark, Ink),
    BOIS(WoodLight, WoodDark, Cream),
    /** Fond transparent, contour et texte à la couleur du contenu. */
    CONTOUR(Color.Transparent, Color.Transparent, null),
}

/** Gros bouton arrondi avec un rebord foncé en dessous, comme un bouton en relief. */
@Composable
fun PirateButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: PirateButtonStyle = PirateButtonStyle.OR,
    enabled: Boolean = true,
    compact: Boolean = false,
) {
    val shape = RoundedCornerShape(if (compact) 12.dp else 18.dp)
    val textColor = style.text ?: LocalContentColor.current
    val lip = if (style == PirateButtonStyle.CONTOUR) 0.dp else if (compact) 3.dp else 5.dp
    Box(
        modifier
            .alpha(if (enabled) 1f else 0.45f)
            .clip(shape)
            .background(style.lip)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(bottom = lip),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(style.face)
                .then(if (style == PirateButtonStyle.CONTOUR) Modifier.border(2.dp, textColor, shape) else Modifier)
                .padding(vertical = if (compact) 8.dp else 15.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                color = textColor,
                style = if (compact) MaterialTheme.typography.labelLarge else MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** Bouton rond de l'en-tête (←, ☰). */
@Composable
fun RoundIconButton(
    glyph: String,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    background: Color = Color.White.copy(alpha = 0.12f),
    enabled: Boolean = true,
) {
    Box(
        modifier
            .size(44.dp)
            .alpha(if (enabled) 1f else 0.4f)
            .clip(CircleShape)
            .background(background)
            .clickable(enabled = enabled, onClickLabel = contentDescription, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(glyph, fontSize = 22.sp, color = LocalContentColor.current)
    }
}

/** Petite étiquette arrondie (« Tour 6 », « Barbe-Grise tient l'île »). */
@Composable
fun Pill(
    text: String,
    modifier: Modifier = Modifier,
    background: Color = Color.White.copy(alpha = 0.12f),
    color: Color = LocalContentColor.current,
) {
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier
            .clip(CircleShape)
            .background(background)
            .padding(horizontal = 14.dp, vertical = 6.dp),
    )
}

/**
 * Carte arrondie des maquettes. Sélectionnée : contour doré. [badge] s'affiche à cheval
 * sur le bord du haut (« CHOISI », « SUR L'ÎLE »).
 */
@Composable
fun PirateCard(
    modifier: Modifier = Modifier,
    color: Color = Sand,
    contentColor: Color = Ink,
    borderColor: Color = Color.Transparent,
    selected: Boolean = false,
    badge: String? = null,
    badgeColor: Color = Gold,
    badgeAlignment: Alignment = Alignment.TopEnd,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    Box(modifier) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = if (badge != null) 10.dp else 0.dp)
                .clip(shape)
                .background(color)
                .border(if (selected) 3.dp else 1.dp, if (selected) Gold else borderColor, shape)
                .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
                .padding(14.dp),
            content = { CompositionLocalProvider(LocalContentColor provides contentColor) { content() } },
        )
        if (badge != null) {
            Text(
                text = badge,
                color = Ink,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .align(badgeAlignment)
                    .offset(x = if (badgeAlignment == Alignment.TopEnd) (-10).dp else 0.dp)
                    .clip(CircleShape)
                    .background(badgeColor)
                    .padding(horizontal = 10.dp, vertical = 3.dp),
            )
        }
    }
}

/** Rond à la couleur du navire avec l'initiale du capitaine. */
@Composable
fun CaptainAvatar(captain: Captain, modifier: Modifier = Modifier, size: Dp = 40.dp) {
    Box(
        modifier
            .size(size)
            .clip(CircleShape)
            .background(captain.ship.color())
            .border(2.dp, Cream.copy(alpha = 0.8f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = captain.name.trim().take(1).uppercase(),
            color = Cream,
            fontFamily = Pirata,
            fontSize = (size.value * 0.55f).sp,
        )
    }
}

/** Barre d'or : progression vers les 20 pièces. */
@Composable
fun GoldBar(gold: Int, modifier: Modifier = Modifier, height: Dp = 10.dp, track: Color = SandDark.copy(alpha = 0.5f)) {
    val fraction = (gold.toFloat() / GameRules.GOLD_TO_WIN).coerceIn(0f, 1f)
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(track),
    ) {
        if (fraction > 0f) {
            Box(
                Modifier
                    .fillMaxWidth(fraction)
                    .height(height)
                    .clip(CircleShape)
                    .background(Gold),
            )
        }
    }
}

/** La coque en cases : une case pleine par point de coque. */
@Composable
fun HullSegments(hull: Int, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(GameRules.MAX_HULL) { i ->
            Box(
                Modifier
                    .size(width = 11.dp, height = 14.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (i < hull) Wood else SandDark.copy(alpha = 0.45f)),
            )
        }
    }
}

/** Petite pièce d'or pour les compteurs. */
@Composable
fun CoinIcon(modifier: Modifier = Modifier, size: Dp = 14.dp) {
    Box(
        modifier
            .size(size)
            .clip(CircleShape)
            .background(Gold)
            .border(1.5.dp, GoldDark, CircleShape),
    )
}

/** Compteur de gourdes : pastille bleue avec la gourde. */
@Composable
fun GourdBadge(gourds: Int, modifier: Modifier = Modifier) {
    Row(
        modifier
            .clip(CircleShape)
            .background(GourdBlue)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Cream),
            contentAlignment = Alignment.Center,
        ) {
            Image(painterResource(R.drawable.ic_die_gourde), contentDescription = "Gourdes", modifier = Modifier.size(18.dp))
        }
        Text(gourds.toString(), color = Cream, style = MaterialTheme.typography.titleMedium)
    }
}
