package com.example.ileauxtresors.ui.components

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.ileauxtresors.ui.theme.Cream
import com.example.ileauxtresors.ui.theme.Ink
import com.example.ileauxtresors.ui.theme.Night
import com.example.ileauxtresors.ui.theme.NightDeep
import com.example.ileauxtresors.ui.theme.Parchment
import com.example.ileauxtresors.ui.theme.Wood
import com.example.ileauxtresors.ui.theme.WoodDark

/** Les trois fonds des maquettes, et la couleur du texte qui va dessus. */
enum class Backdrop(val brush: Brush, val content: Color) {
    NUIT(Brush.verticalGradient(listOf(Night, NightDeep)), Cream),
    PARCHEMIN(Brush.verticalGradient(listOf(Parchment, Parchment)), Ink),
    TAVERNE(Brush.verticalGradient(listOf(Wood, WoodDark)), Cream),
}

/**
 * Cadre commun à toutes les pages : le fond plein écran (sous la barre d'état), un en-tête,
 * le contenu qui défile, et les boutons du bas qui restent fixes.
 */
@Composable
fun PageScaffold(
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit = {},
    /** Dessin plein écran derrière le contenu (le ciel de l'accueil). */
    scenery: @Composable () -> Unit = {},
    bottomBar: @Composable ColumnScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    LightStatusBar(light = backdrop == Backdrop.PARCHEMIN)
    CompositionLocalProvider(LocalContentColor provides backdrop.content) {
        Box(
            modifier
                .fillMaxSize()
                .background(backdrop.brush),
        ) {
            scenery()
            Column(
                Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing),
            ) {
                Box(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) { header() }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    content = content,
                )
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    content = bottomBar,
                )
            }
        }
    }
}

/** En-tête : un bouton à gauche, le titre au centre, un bouton à droite. */
@Composable
fun PageHeader(
    title: String,
    modifier: Modifier = Modifier,
    start: @Composable () -> Unit = {},
    end: @Composable () -> Unit = {},
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(width = 88.dp, height = 44.dp), contentAlignment = Alignment.CenterStart) { start() }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        Box(Modifier.size(width = 88.dp, height = 44.dp), contentAlignment = Alignment.CenterEnd) { end() }
    }
}

/** Icônes de la barre d'état foncées sur parchemin, claires sur fond sombre. */
@Composable
private fun LightStatusBar(light: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = light
    }
}
