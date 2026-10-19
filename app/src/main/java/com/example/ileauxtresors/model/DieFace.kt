package com.example.ileauxtresors.model

/**
 * Les 6 faces d'un dé (équivalent des faces de King of Tokyo).
 *
 * TODO(groupe) : la maquette du plateau affiche bombe / potion / marteau
 * au lieu de canon / gourde / charpentier. Choisir les noms définitifs.
 */
enum class DieFace(val label: String) {
    CUIVRE("1"),
    ARGENT("2"),
    OR("3"),
    CANON("Canon"),
    CHARPENTIER("Charpentier"),
    GOURDE("Gourde"),
}
