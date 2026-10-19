package com.example.ileauxtresors.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.ileauxtresors.navigation.PageGroup

// Code couleur de la maquette de navigation.
val MenuBlue = Color(0xFF2E6DB0)
val PartieGreen = Color(0xFF1E7A78)
val FinOrange = Color(0xFFE08A1E)

val Gold = Color(0xFFF2C14E)
val NightSea = Color(0xFF13233A)

fun PageGroup.color(): Color = when (this) {
    PageGroup.MENU -> MenuBlue
    PageGroup.PARTIE -> PartieGreen
    PageGroup.FIN -> FinOrange
}
