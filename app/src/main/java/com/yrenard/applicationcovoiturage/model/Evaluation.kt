package com.yrenard.applicationcovoiturage.model
/**
 * Classe qui représente la note qu'un passager a accordé pour son trajet.
 *
 */
data class Evaluation(
    /**
     *  Identifiant du passager qui a donné la note.
     */
    val idUsager: Int,
    val note: Int
)
