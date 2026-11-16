# Île aux Trésors

Jeu de dés pour Android inspiré de *King of Tokyo*, version pirate : le joueur affronte 3 capitaines contrôlés par l'appli pour le contrôle d'une île.

Membres du groupe : voir [AUTHORS](AUTHORS).

## Le jeu

- 4 capitaines, 1 île. Le premier à **20 pièces d'or** gagne, ou le dernier navire à flot.
- Chaque navire commence avec 10 points de coque, 0 or et 0 gourde.
- À chaque tour, on lance 6 dés (1 lancer + 2 relances) en gardant ceux qu'on veut :
  - **Cuivre / Argent / Or** : 3 faces identiques rapportent 1 / 2 / 3 or, +1 par face identique en plus.
  - **Canon** : 1 dégât. Depuis l'île, touche tous les autres ; au large, touche celui qui est sur l'île.
  - **Charpentier** : répare 1 point de coque (impossible sur l'île).
  - **Gourde** : monnaie de la Taverne du Butin.
- Sur l'île, on gagne de l'or à chaque tour, mais on ne peut pas se réparer. Touché sur l'île, on choisit de rester ou de fuir.
- À la **Taverne du Butin**, on dépense ses gourdes pour des cartes à effet immédiat (or, réparation, bordée, pillage), ou on balaie les 3 cartes visibles pour 2 gourdes.

Les règles complètes sont aussi disponibles dans l'appli (bouton « Règles » ou menu ☰ en partie).

## Lancer le projet

Prérequis : Android Studio récent, SDK Android 37. L'appli fonctionne à partir d'Android 8 (API 26).

1. Ouvrir le dossier dans Android Studio et laisser Gradle se synchroniser.
2. Choisir un émulateur ou un appareil, puis **Run ▶**.

En ligne de commande :

```bash
./gradlew installDebug       # compile et installe sur l'appareil connecté
./gradlew testDebugUnitTest  # lance les tests unitaires
```

## Architecture

Une seule activité (`MainActivity`), interface en **Jetpack Compose** (Material 3), architecture **MVVM**.

```
Pages (Compose)  →  GameViewModel  →  GameEngine  →  nouveau GameState  →  l'UI se redessine
```

- Le moteur de jeu (`GameEngine`) est fait de fonctions pures : il prend un `GameState` et en renvoie un nouveau. Les règles se testent sans lancer l'appli.
- Les bots utilisent exactement les mêmes règles que le joueur.
- La navigation suit la maquette `maquette_navigation.pdf` : chaque écran correspond à une valeur de l'enum `Page`.

## Organisation des dossiers

Code dans `app/src/main/java/com/example/ileauxtresors/` :

| Dossier | Rôle |
|---|---|
| `MainActivity.kt` | Point d'entrée, associe chaque page à son écran |
| `navigation/` | L'enum `Page` : ACCUEIL, REGLES, CAPITAINE, PLATEAU, TAVERNE, ATTAQUE, FIN |
| `model/` | Données et règles du jeu, sans code Android (`GameState`, `GameEngine`, `GameRules`, `DieFace`, `ButinCard`…) |
| `bots/` | IA des bots (`BotPlayer`) et résumé de leurs tours (`BotLog`) |
| `ui/GameViewModel.kt` | État de la partie, navigation, déroulement du tour des bots |
| `ui/pages/` | Un fichier par écran |
| `ui/components/` | Composants réutilisables : cadre commun, boutons, dés, illustrations dessinées en Canvas |
| `ui/theme/` | Couleurs et thème |

Autres dossiers :

- `app/src/main/res/` : polices, icônes des dés, chaînes de caractères.
- `app/src/test/` : tests unitaires du moteur et des bots.
- `docs/maquettes/` et `maquette_navigation.pdf` : maquettes des écrans et schéma de navigation.

## Polices

`app/src/main/res/font/` : [Pirata One](https://fonts.google.com/specimen/Pirata+One) (titres) et [Nunito](https://fonts.google.com/specimen/Nunito) (texte), toutes deux sous licence SIL Open Font License 1.1.
