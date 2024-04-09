package com.yrenard.applicationcovoiturage.model.data.DatabaseModel

import android.provider.BaseColumns

object DatabaseInfo {
    object UsagerTable : BaseColumns{
        const val TABLE_NAME = "usager"
        const val COLUMN_ID_USAGER = "idusager"
        const val COLUMN_NOM = "nom"
        const val COLUMN_PRENOM = "prenom"
        const val COLUMN_COURRIEL = "courriel"
        const val COLUMN_MOT_DE_PASSE = "motdepasse"
        const val COLUMN_NUMERO_TELEPHONE = "numerotelephone"
    }

    object VoyageTable : BaseColumns{
        const val TABLE_NAME = "voyage"
        const val COLUMN_ID_VOYAGE = "idvoyage"
        const val COLUMN_ID_CONDUCTEUR = "idconducteur"
        const val COLUMN_VILLE_DEPART = "villedepart"
        const val COLUMN_ADRESSE_DEPART = "adressedepart"
        const val COLUMN_DATE = "datevoyage"
        const val COLUMN_ADRESSE_ARRIVEE = "adressearrivee"
        const val COLUMN_CEGEP_ARRIVEE = "cegeparrivee"
        const val COLUMN_NB_MAX_PASSAGER = "nbmaxpassager"
        const val COLUMN_NON_FUMEUR = "nonfumeur"
        const val COLUMN_ACCEPTE_BAGAGE = "acceptebagage"
        const val COLUMN_AIR_CLIMATISE = "airclimatise"
        const val COLUMN_PRICE = "price"
    }

    object PassagerVoyageTable : BaseColumns {
        const val TABLE_NAME = "PassagerVoyage"
        const val COLUMN_ID_PASSAGER_VOYAGE = "idpassagervoyage"
        const val COLUMN_ID_VOYAGE = "idvoyage"
        const val COLUMN_ID_USAGER = "idusager"
    }

    object EvaluationTable : BaseColumns {
        const val TABLE_NAME = "Evaluation"
        const val COLUMN_ID_EVALUATION = "idevaluation"
        const val COLUMN_ID_USAGER_RECEVEUR = "idusagerreceveur"
        const val COLUMN_ID_USAGER_DONNEUR = "idusagerdonneur"
        const val COLUMN_NOTE = "note"
    }
}