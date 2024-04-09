package com.yrenard.applicationcovoiturage

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yrenard.applicationcovoiturage.model.Evaluation
import com.yrenard.applicationcovoiturage.model.Usager
import com.yrenard.applicationcovoiturage.model.Voyage
import com.yrenard.applicationcovoiturage.model.data.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class BDTest {

    val CTX: Context = ApplicationProvider.getApplicationContext()

    val usager1: Usager =
        Usager(1, "Kim", "Potvin", "jean@gmail.com", "test", "4185551111", ArrayList())
    val usager2: Usager =
        Usager(2, "Steeven", "St-Hilaire", "paul@gmail.com", "test", "4185551112", ArrayList())
    val usager3: Usager =
        Usager(3, "Gontran", "Bérubé", "gontran@gmail.com", "test", "4185551113", ArrayList())
    val usager4: Usager = Usager(
        4,
        "Pewdiepie",
        "-",
        "youtubeurtresriche@gmail.com",
        "test",
        "4185551119",
        ArrayList()
    )
    val usager5: Usager =
        Usager(5, "Justin", "Bieber", "eurk@gmail.com", "test", "4185551117", ArrayList())

    val voyage1: Voyage = Voyage(
        1,
        "Dolbeau-Mistassini",
        "Cégep de St-Félicien",
        LocalDate.now(),
        "1731 Boul. Wallberg",
        "1105 Boul. Hamel",
        usager1.id,
        4,
        ArrayList(),
        nonFumeur = false,
        accepteBagage = true,
        aAirClimatise = false
    )
    val voyage2: Voyage = Voyage(
        2,
        "Dolbeau-Mistassini",
        "Cégep de St-Félicien",
        LocalDate.now(),
        "1500 Boul. Wallberg",
        "1105 Boul. Hamel",
        usager2.id,
        2,
        ArrayList(),
        nonFumeur = true,
        accepteBagage = true,
        aAirClimatise = false
    )
    val voyage3: Voyage = Voyage(
        3,
        "Roberval",
        "Cégep de St-Félicien",
        LocalDate.now().minusDays(2),
        "1200 Boul. Thivierge",
        "1105 Boul. Hamel",
        usager3.id,
        7,
        ArrayList(),
        nonFumeur = true,
        accepteBagage = true,
        aAirClimatise = false
    )

    val voyage4: Voyage = Voyage(
        4,
        "Roberval",
        "Cégep de St-Félicien",
        LocalDate.now().minusDays(2),
        "1200 Boul. Youtube",
        "1105 Boul. Hamel",
        usager4.id,
        5,
        ArrayList(),
        nonFumeur = true,
        accepteBagage = true,
        aAirClimatise = false
    )


    val voyage5: Voyage = Voyage(
        5,
        "Dolbeau-Mistassini",
        "Cégep de St-Félicien",
        LocalDate.now().minusDays(3),
        "1200 Boul. Hollywood",
        "1105 Boul. Hamel",
        usager5.id,
        1,
        ArrayList(),
        nonFumeur = true,
        accepteBagage = true,
        aAirClimatise = false
    )

    /**
     * Recreate the tables in the database for each test
     */
    @Before
    fun createNewDB() {
        DatabaseHandler.deleteDBAndRecreate(this.CTX)
    }

    /**
     * Delete all table in the database after a test
     */
    @After
    fun deleteBD() {
        DatabaseHandler.deleteDB(this.CTX)
    }

    /**
     * Test to see if we sign in a user if it correctly inserted
     */
    @Test
    fun signInUser() {
        DatabaseHandler.creeOrUpdateUsager(this.CTX, usager1)
        DatabaseHandler.creeOrUpdateUsager(this.CTX, usager2)
        DatabaseHandler.creeOrUpdateUsager(this.CTX, usager3)

        val usagers = DatabaseHandler.getUsagers(this.CTX)
        assert(usagers.size == 3)
        assert(usager1 == usagers[0])
        assert(usager2 == usagers[1])
        assert(usager3 == usagers[2])
    }

    /**
     * Test to see if we create a travel if it correctly inserted and link to the good user
     */
    @Test
    fun createTravel() {
        DatabaseHandler.creeOrUpdateUsager(this.CTX, usager1)
        DatabaseHandler.creeOrUpdateUsager(this.CTX, usager2)
        DatabaseHandler.creeOrUpdateUsager(this.CTX, usager3)
        DatabaseHandler.creeOrUpdateUsager(this.CTX, usager4)

        DatabaseHandler.creeVoyage(this.CTX, voyage1)
        DatabaseHandler.creeVoyage(this.CTX, voyage2)
        DatabaseHandler.creeVoyage(this.CTX, voyage3)
        DatabaseHandler.creeVoyage(this.CTX, voyage4)

        val voyages = DatabaseHandler.getVoyages(this.CTX)

        assert(voyages.size == 4)
        assert(voyages[0].id == voyage1.id)
        assert(voyages[1].adresseArrivee == voyage2.adresseArrivee)
        assert(voyages[2].idConducteur == voyage3.idConducteur)
        assert(voyages[3].nbPlaceRestante() == voyage4.nbPlaceRestante())
    }

    /**
     * Test to see if we can correctly join travel, set a note (evaluation) and leave travel
     */
    @Test
    fun joinNoteLeaveTravel() {
        assert(
            DatabaseHandler.creeOrUpdateUsager(
                this.CTX,
                usager4
            )
        )
        assert(
            DatabaseHandler.creeOrUpdateUsager(
                this.CTX,
                usager5
            )
        )

        assert(DatabaseHandler.creeVoyage(this.CTX, voyage4))

        val voyageInit =
            DatabaseHandler.getVoyages(this.CTX)[0]

        val resultJoined = DatabaseHandler.joindreVoyage(
            null,
            this.CTX,
            voyageInit.id,
            usager5.id
        )
        assert(resultJoined != -1L)

        val voyageJoined =
            DatabaseHandler.getVoyages(this.CTX)[0]

        assert(voyageInit.nbPlaceRestante() - 1 == voyageJoined.nbPlaceRestante())

        val eval = Evaluation(usager5.id, 19)

        assert(
            DatabaseHandler.quitterVoyage(
                this.CTX,
                voyageJoined.id,
                usager5.id
            ) != -1
        )
        assert(
            DatabaseHandler.insertEvaluation(
                this.CTX,
                usager4.id,
                eval
            ) != -1L
        )

        val evalGetted =
            DatabaseHandler.getUsagers(this.CTX)[0].listeEvaluations[0]

        assert(evalGetted.note == 19)
    }

    /**
     * Test to see if we can updateValue of a user and get the same id after the update
     */
    @Test
    fun updateUser() {
        assert(DatabaseHandler.creeOrUpdateUsager(this.CTX, usager1))

        val usagerInserted: Usager = DatabaseHandler.getUsagers(this.CTX)[0]

        usagerInserted.nom = "NewName"
        usagerInserted.courriel = "newemail@email.mail"

        assert(DatabaseHandler.creeOrUpdateUsager(this.CTX, usagerInserted, TypeInsertOrUpdate.UPDATE))

        val usagerInsertedModified: Usager = DatabaseHandler.getUsagers(this.CTX)[0]

        assert(usagerInsertedModified.id == usager1.id)
        assert(usagerInsertedModified.nom == "NewName")
        assert(usagerInsertedModified.courriel == "newemail@email.mail")
    }

    /**
     * Test to see if we delete a user if everything that the user was in has been delete
     */
    @Test
    fun deleteUserIfInTravel() {
        assert(
            DatabaseHandler.creeOrUpdateUsager(
                this.CTX,
                usager3
            )
        )
        assert(
            DatabaseHandler.creeOrUpdateUsager(
                this.CTX,
                usager4
            )
        )

        assert(DatabaseHandler.creeVoyage(this.CTX, voyage3))

        val voyageInserted =
            DatabaseHandler.getVoyages(this.CTX)[0]

        val resultJoined = DatabaseHandler.joindreVoyage(
            null,
            this.CTX,
            voyageInserted.id,
            usager4.id
        )

        assert(resultJoined != -1L)

        val usagerInserted = DatabaseHandler.getUsagers(this.CTX)[1]

        assert(usagerInserted.id == usager4.id)

        assert(DatabaseHandler.deleteUsager(this.CTX, usagerInserted.id))

        val voyageAfterUserRemoved = DatabaseHandler.getVoyages(this.CTX)[0]

        assert(voyageAfterUserRemoved.id == voyage3.id)

        assert(voyageAfterUserRemoved.nbPlaceRestante() == voyageInserted.nbPlaceRestante())
    }

}