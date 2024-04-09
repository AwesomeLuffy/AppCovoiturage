package com.yrenard.applicationcovoiturage.model

import java.time.LocalDate
import kotlin.collections.ArrayList
/**
 * Classe qui représente une annonce de covoiturage.
 *
 */
data class Voyage(var id: Int,
                  var villeDepart: String,
                  var cegepArrivee: String,
                  var date: LocalDate,
                  var adresseDepart: String,
                  var adresseArrivee: String,
                  var idConducteur: Int,
                  var nbMaxPassager: Int,
                  var listeIdPassagers: ArrayList<Int>,
                  var nonFumeur: Boolean,
                  var accepteBagage: Boolean,
                  var aAirClimatise: Boolean,
                  var prix: Float = 0.0f
                  ) : java.io.Serializable
{
    /**
     * Fonction qui retourne le nombre de places restantes dans le véhicule
     * en tenant compte du nombre de passagers ayant joint le covoiturage.
     *
     */
    fun nbPlaceRestante(): Int {
        return nbMaxPassager - listeIdPassagers.size
    }
}
