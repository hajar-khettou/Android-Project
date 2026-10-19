package com.example.ileauxtresors.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ColorScheme = lightColorScheme(
    primary = MenuBlue,
    secondary = PartieGreen,
    tertiary = FinOrange,
)

@Composable
fun IleAuxTresorsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography(),
        content = content,
    )
}
