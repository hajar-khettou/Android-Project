package com.example.ileauxtresors.navigation

/** Groupe de couleur de la maquette : bleu = menus, vert = partie, orange = fin. */
enum class PageGroup { MENU, PARTIE, FIN }

/** Les 7 pages de l'appli, toutes affichées dans MainActivity (voir maquette_navigation.pdf). */
enum class Page(val group: PageGroup) {
    ACCUEIL(PageGroup.MENU),
    REGLES(PageGroup.MENU),
    CAPITAINE(PageGroup.MENU),
    PLATEAU(PageGroup.PARTIE),
    TAVERNE(PageGroup.PARTIE),
    ATTAQUE(PageGroup.PARTIE),
    FIN(PageGroup.FIN),
}
