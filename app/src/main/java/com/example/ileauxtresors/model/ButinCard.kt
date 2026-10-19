package com.example.ileauxtresors.model

/** Ce que fait une carte Butin quand on l'achète. */
sealed interface ButinEffect {
    data class GainGold(val amount: Int) : ButinEffect
    data class Repair(val amount: Int) : ButinEffect
    data class DamageOthers(val amount: Int) : ButinEffect
    /** Chaque autre navire perd [amount] or (s'il en a), le joueur gagne tout ce qui est pris. */
    data class Plunder(val amount: Int) : ButinEffect
}

/**
 * Une carte Butin de la Taverne (équivalent des cartes pouvoir de King of Tokyo).
 * Elle s'achète avec des gourdes et son effet s'applique tout de suite.
 */
data class ButinCard(
    val name: String,
    val cost: Int,
    val description: String,
    val effect: ButinEffect,
)

/**
 * Le paquet de départ.
 * TODO(groupe) : proposition à valider ; ajouter des cartes « à garder » (effet permanent).
 */
object ButinDeck {
    private val kinds = listOf(
        ButinCard("Bourse de doublons", 3, "+1 or", ButinEffect.GainGold(1)),
        ButinCard("Coffre enterré", 5, "+3 or", ButinEffect.GainGold(3)),
        ButinCard("Carte au trésor", 7, "+4 or", ButinEffect.GainGold(4)),
        ButinCard("Tonneau de rhum", 3, "Répare 2 coque", ButinEffect.Repair(2)),
        ButinCard("Charpentiers du port", 5, "Répare 4 coque", ButinEffect.Repair(4)),
        ButinCard("Bordée de canons", 5, "Chaque autre navire perd 2 coque", ButinEffect.DamageOthers(2)),
        ButinCard("Pillage", 4, "Prend 1 or à chaque autre navire", ButinEffect.Plunder(1)),
    )

    /** Deux exemplaires de chaque carte. */
    val cards: List<ButinCard> = kinds + kinds
}
