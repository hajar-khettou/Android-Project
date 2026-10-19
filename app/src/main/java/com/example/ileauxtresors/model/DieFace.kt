package com.example.ileauxtresors.model

/**
 * Les 6 faces d'un dé (équivalent des faces de King of Tokyo).
 * [coinValue] : or rapporté par une série de 3 faces identiques (0 pour les faces spéciales).
 */
enum class DieFace(val label: String, val coinValue: Int = 0) {
    CUIVRE("1", coinValue = 1),
    ARGENT("2", coinValue = 2),
    OR("3", coinValue = 3),
    CANON("Canon"),
    CHARPENTIER("Charpentier"),
    GOURDE("Gourde"),
}
