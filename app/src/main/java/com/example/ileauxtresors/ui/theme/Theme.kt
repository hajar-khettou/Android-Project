package com.example.ileauxtresors.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ileauxtresors.R

/** Police gothique des titres. */
val Pirata = FontFamily(Font(R.font.pirata_one))

/** Police arrondie du texte (police variable : une seule police pour toutes les graisses). */
@OptIn(ExperimentalTextApi::class)
val Nunito = FontFamily(
    listOf(FontWeight.Normal, FontWeight.SemiBold, FontWeight.Bold, FontWeight.ExtraBold).map { weight ->
        Font(R.font.nunito, weight, variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)))
    },
)

private fun pirata(size: Int) = TextStyle(fontFamily = Pirata, fontSize = size.sp, lineHeight = (size * 1.05).sp)

private fun nunito(size: Int, weight: FontWeight = FontWeight.Normal) =
    TextStyle(fontFamily = Nunito, fontWeight = weight, fontSize = size.sp, lineHeight = (size * 1.35).sp)

private val PirateTypography = Typography(
    displayLarge = pirata(64),
    displayMedium = pirata(44),
    headlineLarge = pirata(36),
    headlineMedium = pirata(30),
    titleLarge = pirata(24),
    titleMedium = nunito(17, FontWeight.ExtraBold),
    titleSmall = nunito(14, FontWeight.Bold),
    bodyLarge = nunito(16),
    bodyMedium = nunito(14),
    bodySmall = nunito(12),
    labelLarge = nunito(14, FontWeight.Bold),
    labelMedium = nunito(12, FontWeight.Bold),
    labelSmall = nunito(10, FontWeight.ExtraBold),
)

private val ColorScheme = lightColorScheme(
    primary = Gold,
    onPrimary = Ink,
    secondary = Wood,
    onSecondary = Cream,
    tertiary = Sea,
    background = Night,
    onBackground = Cream,
    surface = Parchment,
    onSurface = Ink,
    surfaceContainer = Parchment,
    outline = SandDark,
)

@Composable
fun IleAuxTresorsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = PirateTypography,
        content = content,
    )
}
