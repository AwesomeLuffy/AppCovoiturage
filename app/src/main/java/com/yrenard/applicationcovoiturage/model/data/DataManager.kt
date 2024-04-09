package com.yrenard.applicationcovoiturage.model.data

import com.yrenard.applicationcovoiturage.ApplicationCoVoiturage
import com.yrenard.applicationcovoiturage.model.Recherche
import com.yrenard.applicationcovoiturage.model.Usager
import com.yrenard.applicationcovoiturage.model.Voyage

/**
 * Classe centrale de gestion des données.
 * Implémente le patron de conception Singleton.
 * N.B: Pour le TP1, ne modifiez pas cette classe.
 */
object DataManager {
    var idConnectedUser: Int = -1

    var listeVoyages: ArrayList<Voyage> = ArrayList()
    var listeUsagers: ArrayList<Usager> = ArrayList()
    var listeCegeps: ArrayList<String> = ArrayList()

    val defaultUserError: Usager =
        Usager(-1, "", "", "", "", "", ArrayList())

    /**
     * Constructeur par bloc d'initialization.
     * Appelé une seule fois dans l'application,
     * à la création de l'instance unique du Data Manager.
     */
    init {
        initialiserCegeps()
        initialiserVoyages()
        initialiserUsagers()
    }

    fun getUsagerListFromVoyages(voyage: Voyage): ArrayList<String> {
        val array: ArrayList<String> = ArrayList()
        var actualUser: Usager
        for (id: Int in voyage.listeIdPassagers) {
            if (id == voyage.idConducteur) {
                continue
            }
            actualUser = getUsager(id)
            array.add("${actualUser.nom} ${actualUser.prenom}")
        }

        return array
    }

    /**
     * Initialise la liste des cégeps qui sera utilisé pour le spinner de recherche.
     */
    private fun initialiserCegeps() {
        listeCegeps.add("Cégep de St-Félicien")
        listeCegeps.add("Collège d'Alma")
        listeCegeps.add("Cégep de Chicoutimi")
        listeCegeps.add("Cégep de Jonquière")
        listeCegeps.add("Centre d'études de Chibougameau")
        listeCegeps.add("League of Legends :)")
        listeCegeps.add("Final Fantasy 14 :) ")
        listeCegeps.add("Diablo Immortals :)")
    }

    /**
     * Fonction que vous devez appeler dans ResultatActivity.
     * Elle retourne la liste des trajets qui correspondent à des critères de recherche.
     */
    fun rechercheVoyage(recherche: Recherche): ArrayList<Voyage> {

        val resultat: ArrayList<Voyage> = ArrayList()
        // On parcourt la liste de trajets et on retourne ceux qui correspondent aux filtres de recherche.
        for (voyage: Voyage in listeVoyages) {
            if (recherche.villeDepart != voyage.villeDepart || recherche.cegepArrivee != voyage.cegepArrivee
                || recherche.dateAnnee != voyage.date.year || recherche.dateJour != voyage.date.dayOfMonth
                || recherche.dateMois != voyage.date.monthValue || voyage.nbPlaceRestante() < recherche.nbPassagers
                || (recherche.nonFumeur && !voyage.nonFumeur)
                || (recherche.accepteBagage && !voyage.accepteBagage)
                || (recherche.aAirClimatise && !voyage.aAirClimatise)
            ) {
                continue // On passe au suivant sans ajouter le trajet à la liste des résultats.
            }

            resultat.add(voyage)
        }
        return resultat
    }

    fun initialiserVoyages() {
        listeVoyages = DatabaseHandler.getVoyages(ApplicationCoVoiturage.getApplicationContext())
    }

    fun initialiserUsagers() {
        listeUsagers = DatabaseHandler.getUsagers(ApplicationCoVoiturage.getApplicationContext())
    }

    /**
     * Initialise une liste prédéfinie de trajets de covoiturage et d'usagers
     * pour que vous puissiez tester votre application.
     */
//    fun initialiserVoyages() {
//        // Liste des utilisateurs.
//        val usager1: Usager = Usager(0, "Kim", "Potvin","jean@gmail.com", "test", "4185551111", ArrayList())
//        val usager2: Usager = Usager(1, "Steeven", "St-Hilaire","paul@gmail.com", "test", "4185551112", ArrayList())
//        val usager3: Usager = Usager(2, "Gontran", "Bérubé","gontran@gmail.com", "test", "4185551113", ArrayList())
//        val usager4: Usager = Usager(3, "Pewdiepie", "-","youtubeurtresriche@gmail.com", "test", "4185551119", ArrayList())
//        val usager5: Usager = Usager(4, "Justin", "Bieber","eurk@gmail.com", "test", "4185551117", ArrayList())
//        listeUsagers.add(usager1)
//        listeUsagers.add(usager2)
//        listeUsagers.add(usager3)
//        listeUsagers.add(usager4)
//        listeUsagers.add(usager5)
//
//        // Liste des offres de covoiturage.
//        DataManager.listeVoyages = DatabaseHandler
//            .getVoyages(ApplicationCoVoiturage.getApplicationContext())
//
//        val voyage1: Voyage = Voyage(
//            0,
//            "Dolbeau-Mistassini",
//            "Cégep de St-Félicien",
//            LocalDate.now(),
//            "1731 Boul. Wallberg",
//            "1105 Boul. Hamel",
//            usager1.id,
//            4,
//            ArrayList(),
//            nonFumeur = false,
//            accepteBagage = true,
//            aAirClimatise = false)
//        val voyage2: Voyage = Voyage(
//            1,
//            "Dolbeau-Mistassini",
//            "Cégep de St-Félicien",
//            LocalDate.now(),
//            "1500 Boul. Wallberg",
//            "1105 Boul. Hamel",
//            usager2.id,
//            2,
//            ArrayList(),
//            nonFumeur = true,
//            accepteBagage = true,
//            aAirClimatise = false
//        )
//        val voyage3: Voyage = Voyage(
//            2,
//            "Roberval",
//            "Cégep de St-Félicien",
//            LocalDate.now().minusDays(2),
//            "1200 Boul. Thivierge",
//            "1105 Boul. Hamel",
//            usager3.id,
//            7,
//            ArrayList(),
//            nonFumeur = true,
//            accepteBagage = true,
//            aAirClimatise = false
//        )
//        voyage3.listeIdPassagers.add(usager1.id)
//
//        val voyage4: Voyage = Voyage(
//            3,
//            "Roberval",
//            "Cégep de St-Félicien",
//            LocalDate.now().minusDays(2),
//            "1200 Boul. Youtube",
//            "1105 Boul. Hamel",
//            usager4.id,
//            5,
//            ArrayList(),
//            nonFumeur = true,
//            accepteBagage = true,
//            aAirClimatise = false
//        )
//
//
//        val voyage5: Voyage = Voyage(
//            4,
//            "Dolbeau-Mistassini",
//            "Cégep de St-Félicien",
//            LocalDate.now().minusDays(3),
//            "1200 Boul. Hollywood",
//            "1105 Boul. Hamel",
//            usager5.id,
//            1,
//            ArrayList(),
//            nonFumeur = true,
//            accepteBagage = true,
//            aAirClimatise = false
//        )
//
//        listeVoyages.add(voyage1)
//        listeVoyages.add(voyage2)
//        listeVoyages.add(voyage3)
//        listeVoyages.add(voyage4)
//        listeVoyages.add(voyage5)
//
//        // Liste des évaluations.
//        val eval1: Evaluation = Evaluation(usager2.id, 2)
//        val eval2: Evaluation = Evaluation(usager2.id, 4)
//        usager1.listeEvaluations.add(eval1)
//        usager1.listeEvaluations.add(eval2)
//    }

    fun checkUserMailAndPass(
        mail: String,
        password: String = "",
        assignUserId: Boolean = true
    ): Pair<Boolean, Boolean> {
        for (user: Usager in listeUsagers) {
            if (user.courriel == mail) {
                if (password != "") {
                    if (assignUserId) {
                        idConnectedUser = user.id
                    }
                    return Pair(true, (user.motdepasse == password))
                }
                return Pair(true, false)
            }
        }
        return Pair(false, false)
    }

    fun getUsager(id: Int): Usager {
        for (u: Usager in listeUsagers) {
            if (u.id == id) {
                return u
            }
        }
        return defaultUserError
    }

    fun checkIfUserExists(courriel: String): Boolean {
        for (u: Usager in listeUsagers) {
            if (u.courriel == courriel) {
                return true
            }
        }
        return false
    }
}