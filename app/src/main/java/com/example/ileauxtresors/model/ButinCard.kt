package com.example.ileauxtresors.model

/**
 * Une carte Butin de la Taverne (équivalent des cartes pouvoir de King of Tokyo).
 * Elle s'achète avec des gourdes.
 *
 * TODO(groupe) : définir la liste des cartes et leurs effets.
 */
data class ButinCard(
    val name: String,
    val cost: Int,
    val description: String,
)
