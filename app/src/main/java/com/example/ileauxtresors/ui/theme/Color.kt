package com.example.ileauxtresors.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.ileauxtresors.model.Ship

// Couleurs des maquettes d'écran (docs/maquettes).

/** La mer de nuit : fond de l'accueil, du plateau et de la fin. */
val Night = Color(0xFF0F2740)
val NightDeep = Color(0xFF0A1B2D)
val NightLight = Color(0xFF173452)

/** Le parchemin : fond des menus (capitaine, règles) et des cartes. */
val Parchment = Color(0xFFF3E6CC)
val Cream = Color(0xFFFBF3E2)
val Sand = Color(0xFFE8D3A8)
val SandDark = Color(0xFFC9A877)

val Gold = Color(0xFFEBB94A)
val GoldDark = Color(0xFFB8861F)

/** Le bois : bac à dés, Taverne, bouton « Relancer ». */
val Wood = Color(0xFF5A3418)
val WoodLight = Color(0xFF7E4A25)
val WoodDark = Color(0xFF3C2210)

/** Texte foncé sur parchemin. */
val Ink = Color(0xFF2B1A0E)
val InkSoft = Color(0xFF6B5440)

val Sea = Color(0xFF1F7173)
val SeaDark = Color(0xFF144E5A)
val Palm = Color(0xFF3F8A3A)
val GourdBlue = Color(0xFF2F6DB5)
val Danger = Color(0xFFC0392B)

/** Couleur du pavillon de chaque navire. */
fun Ship.color(): Color = when (this) {
    Ship.ANNE_LA_ROUGE -> Color(0xFFC0392B)
    Ship.BARBE_GRISE -> Color(0xFF8A5A3B)
    Ship.MAREE_BLEUE -> GourdBlue
    Ship.JADE_SABRE -> Color(0xFF3F8A3A)
}
