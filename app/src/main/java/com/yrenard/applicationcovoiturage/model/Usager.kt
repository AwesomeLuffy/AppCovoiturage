package com.yrenard.applicationcovoiturage.model

/**
 * Classe qui représente un usager dans l'application.
 *
 */
data class Usager(var id:Int,
                  var prenom: String,
                  var nom: String,
                  var courriel:String,
                  var motdepasse:String,
                  var numeroTelephone:String,
                  var listeEvaluations: ArrayList<Evaluation>)
{
    /**
     * Fonction retournant la note moyenne d'un usager.
     *
     */
    fun scoreMoyen(): Double {
        var resultat : Double = 0.0
        for (ev : Evaluation in listeEvaluations) {
            resultat += ev.note
        }
        return resultat
    }
}
