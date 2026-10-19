package com.example.ileauxtresors.model

/**
 * Les 6 faces d'un dé (équivalent des faces de King of Tokyo).
 * [coinValue] : or rapporté par une série de 3 faces identiques (0 pour les faces spéciales).
 */
enum class DieFace(val coinValue: Int = 0) {
    CUIVRE(coinValue = 1),
    ARGENT(coinValue = 2),
    OR(coinValue = 3),
    CANON,
    CHARPENTIER,
    GOURDE,
}
