package com.yrenard.applicationcovoiturage.model
import com.yrenard.applicationcovoiturage.model.TypeRecherche
import java.io.Serializable

/**
 * Classe qui représente une recherche de trajet.
 *
 */
data class Recherche (
    var typeRecherche: TypeRecherche = TypeRecherche.FILTRE,
    var nbPassagers: Int = -1,
    var villeDepart: String = "",
    var cegepArrivee: String = "",
    var dateJour: Int = -1,
    var dateMois: Int = -1,
    var dateAnnee: Int = -1,
    var nonFumeur: Boolean = false,
    var accepteBagage: Boolean = false,
    var aAirClimatise: Boolean = false
) : Serializable