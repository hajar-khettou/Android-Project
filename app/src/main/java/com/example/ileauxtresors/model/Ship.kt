package com.example.ileauxtresors.model

/** Les 4 navires de l'écran « Choisissez votre navire ». */
enum class Ship(val displayName: String, val subtitle: String) {
    ANNE_LA_ROUGE("Anne la Rouge", "Canonnière redoutable"),
    BARBE_GRISE("Barbe-Grise", "Vieux loup de mer"),
    MAREE_BLEUE("Marée Bleue", "Reine des gourdes"),
    JADE_SABRE("Jade Sabre", "Chasseuse de trésors"),
}
