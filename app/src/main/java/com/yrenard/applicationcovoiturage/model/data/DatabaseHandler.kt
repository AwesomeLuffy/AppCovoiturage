package com.yrenard.applicationcovoiturage.model.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.yrenard.applicationcovoiturage.model.Evaluation
import com.yrenard.applicationcovoiturage.model.Usager
import com.yrenard.applicationcovoiturage.model.Voyage
import java.time.LocalDate
import com.yrenard.applicationcovoiturage.model.data.DatabaseModel.DatabaseInfo as DI

private const val SQL_CREATE_ENTRIES_USAGER_TABLE = """
    CREATE TABLE ${DI.UsagerTable.TABLE_NAME} (
    ${DI.UsagerTable.COLUMN_ID_USAGER} INTEGER PRIMARY KEY AUTOINCREMENT,
    ${DI.UsagerTable.COLUMN_NOM} TEXT NOT NULL,
    ${DI.UsagerTable.COLUMN_PRENOM} TEXT NOT NULL,
    ${DI.UsagerTable.COLUMN_COURRIEL} TEXT NOT NULL,
    ${DI.UsagerTable.COLUMN_MOT_DE_PASSE} TEXT NOT NULL,
    ${DI.UsagerTable.COLUMN_NUMERO_TELEPHONE} TEXT NOT NULL);    
"""

private const val SQL_CREATE_ENTRIES_VOYAGE_TABLE = """
    CREATE TABLE ${DI.VoyageTable.TABLE_NAME} (
    ${DI.VoyageTable.COLUMN_ID_VOYAGE} INTEGER PRIMARY KEY AUTOINCREMENT,
    ${DI.VoyageTable.COLUMN_ID_CONDUCTEUR} INTEGER NOT NULL,
    ${DI.VoyageTable.COLUMN_VILLE_DEPART} TEXT NOT NULL,
    ${DI.VoyageTable.COLUMN_ADRESSE_DEPART} TEXT NOT NULL,
    ${DI.VoyageTable.COLUMN_DATE} TEXT NOT NULL,
    ${DI.VoyageTable.COLUMN_ADRESSE_ARRIVEE} TEXT NOT NULL,
    ${DI.VoyageTable.COLUMN_CEGEP_ARRIVEE} TEXT NOT NULL,
    ${DI.VoyageTable.COLUMN_NB_MAX_PASSAGER} INTEGER NOT NULL,
    ${DI.VoyageTable.COLUMN_NON_FUMEUR} BOOLEAN NOT NULL,
    ${DI.VoyageTable.COLUMN_ACCEPTE_BAGAGE} BOOLEAN NOT NULL,
    ${DI.VoyageTable.COLUMN_AIR_CLIMATISE} BOOLEAN NOT NULL,
    ${DI.VoyageTable.COLUMN_PRICE} REAL NOT NULL,
    FOREIGN KEY (${DI.VoyageTable.COLUMN_ID_CONDUCTEUR}) REFERENCES ${DI.UsagerTable.TABLE_NAME}(${DI.UsagerTable.COLUMN_ID_USAGER})
    );
"""

private const val SQL_CREATE_ENTRIES_PASSAGER_VOYAGEUR_TABLE = """
    CREATE TABLE ${DI.PassagerVoyageTable.TABLE_NAME} (
    ${DI.PassagerVoyageTable.COLUMN_ID_PASSAGER_VOYAGE} INTEGER PRIMARY KEY AUTOINCREMENT,
    ${DI.PassagerVoyageTable.COLUMN_ID_VOYAGE} INTEGER NOT NULL,
    ${DI.PassagerVoyageTable.COLUMN_ID_USAGER} INTEGER NOT NULL,
    FOREIGN KEY (${DI.PassagerVoyageTable.COLUMN_ID_VOYAGE}) REFERENCES ${DI.VoyageTable.TABLE_NAME}(${DI.VoyageTable.COLUMN_ID_VOYAGE}),
    FOREIGN KEY (${DI.PassagerVoyageTable.COLUMN_ID_USAGER}) REFERENCES ${DI.UsagerTable.TABLE_NAME}(${DI.UsagerTable.COLUMN_ID_USAGER})
    );
"""

private const val SQL_CREATE_ENTRIES_EVALUATION_TABLE = """
    CREATE TABLE ${DI.EvaluationTable.TABLE_NAME} (
    ${DI.EvaluationTable.COLUMN_ID_EVALUATION} INTEGER PRIMARY KEY AUTOINCREMENT,
    ${DI.EvaluationTable.COLUMN_ID_USAGER_DONNEUR} INTEGER NOT NULL,
    ${DI.EvaluationTable.COLUMN_ID_USAGER_RECEVEUR} INTEGER NOT NULL,
    ${DI.EvaluationTable.COLUMN_NOTE} TEXT NOT NULL,
    FOREIGN KEY (${DI.EvaluationTable.COLUMN_ID_USAGER_DONNEUR}) REFERENCES ${DI.UsagerTable.TABLE_NAME}(${DI.UsagerTable.COLUMN_ID_USAGER}),
    FOREIGN KEY (${DI.EvaluationTable.COLUMN_ID_USAGER_RECEVEUR}) REFERENCES ${DI.UsagerTable.TABLE_NAME}(${DI.UsagerTable.COLUMN_ID_USAGER})
    );
"""

enum class TypeUsager (val type: String){
    RECEVEUR(DI.EvaluationTable.COLUMN_ID_USAGER_RECEVEUR),
    DONNEUR(DI.EvaluationTable.COLUMN_ID_USAGER_DONNEUR)
}

enum class TypeInsertOrUpdate{
    INSERT,
    UPDATE
}

/**
 * DatabaseHandler to communicate and insert value into the database ;
 * All request in the database create an instance that is deleted after ;
 * In case if there a subrequest in the query that will be use the same instance
 * @property context: Context context of the application
 */
class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        DatabaseHandler.createTables(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldV: Int, newV: Int) {
        if(oldV <= 1){
            db.execSQL("ALTER TABLE ${DI.VoyageTable.TABLE_NAME} ADD ${DI.VoyageTable.COLUMN_PRICE} REAL NOT NULL DEFAULT(0.0);")
        }
    }

    //All function inside are static to allow to create instance for request so the connexion
    // is not open everytime
    companion object{
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "CoVoitDatabase.db"

        /**
         * Method to create or update a user in the database
         *
         * @property context Context of the application
         * @property usager User that have to be inserted
         * @property mode Current mode if we want to INSERT (Default) an user or UPDATE
         * @return True if user has been update or insert
         */
        fun creeOrUpdateUsager(context: Context, usager: Usager, mode: TypeInsertOrUpdate = TypeInsertOrUpdate.INSERT): Boolean{
            val writable = DatabaseHandler(context).writableDatabase
            try {
                val cv: ContentValues = ContentValues()
                if(usager.id != -1){
                    cv.put(DI.UsagerTable.COLUMN_ID_USAGER, usager.id)
                }
                cv.put(DI.UsagerTable.COLUMN_NOM, usager.nom)
                cv.put(DI.UsagerTable.COLUMN_PRENOM, usager.prenom)
                cv.put(DI.UsagerTable.COLUMN_COURRIEL, usager.courriel)
                cv.put(DI.UsagerTable.COLUMN_MOT_DE_PASSE, usager.motdepasse)
                cv.put(DI.UsagerTable.COLUMN_NUMERO_TELEPHONE, usager.numeroTelephone)

                if(mode == TypeInsertOrUpdate.UPDATE){
                    writable.update(DI.UsagerTable.TABLE_NAME,
                        cv,
                        "${DI.UsagerTable.COLUMN_ID_USAGER} = ?",
                        arrayOf(usager.id.toString())
                    ).toLong()
                    DataManager.listeUsagers.remove(usager)
                } else{
                    usager.id = writable.insert(DI.UsagerTable.TABLE_NAME, null, cv).toInt()
                }

                DataManager.listeUsagers.add(usager)

                return true
            }
            catch (e: SQLiteException){
                Log.e("ErrorCoVoit", e.message!!)
                return false
            }
            finally {
                writable.close()
            }
        }

        /**
         * Method to create a travel in the database
         * @property context Context of the application
         * @property voyage Voyage that will be inserted
         * @return True if the travel has been insert
         */
        fun creeVoyage(context: Context, voyage: Voyage): Boolean{
            val writable = DatabaseHandler(context).writableDatabase
            try {
                //Get actual id of the user connected to the app
                if(voyage.idConducteur == -1){
                    voyage.idConducteur = DataManager.idConnectedUser
                }

                val cv: ContentValues = ContentValues()
                if(voyage.id != -1){
                    cv.put(DI.VoyageTable.COLUMN_ID_VOYAGE, voyage.id)
                }
                cv.put(DI.VoyageTable.COLUMN_ID_CONDUCTEUR, voyage.idConducteur)
                cv.put(DI.VoyageTable.COLUMN_VILLE_DEPART, voyage.villeDepart)
                cv.put(DI.VoyageTable.COLUMN_ADRESSE_DEPART, voyage.adresseDepart)
                cv.put(DI.VoyageTable.COLUMN_DATE, voyage.date.toString())
                cv.put(DI.VoyageTable.COLUMN_ADRESSE_ARRIVEE, voyage.adresseArrivee)
                cv.put(DI.VoyageTable.COLUMN_CEGEP_ARRIVEE, voyage.cegepArrivee)
                cv.put(DI.VoyageTable.COLUMN_PRICE, voyage.prix)
                cv.put(DI.VoyageTable.COLUMN_NB_MAX_PASSAGER, voyage.nbMaxPassager)
                cv.put(DI.VoyageTable.COLUMN_NON_FUMEUR, voyage.nonFumeur)
                cv.put(DI.VoyageTable.COLUMN_ACCEPTE_BAGAGE,voyage.accepteBagage)
                cv.put(DI.VoyageTable.COLUMN_AIR_CLIMATISE, voyage.aAirClimatise)

                voyage.id = writable.insert(DI.VoyageTable.TABLE_NAME, null, cv).toInt()

                DataManager.listeVoyages.add(voyage)

//                if(voyage.id != -1){
//                    DatabaseHandler.joindreVoyage(writable, context, voyage.id, voyage.idConducteur)
//                }

                return true
            }
            catch (e: SQLiteException){
                Log.e("ErrorCoVoit", e.message!!)
                return false
            }
            finally {
                writable.close()
            }
        }

        /**
         * Method to get all users in the database
         *
         * @property context Context of the application
         * @return an ArrayList of Users
         */
        fun getUsagers(context: Context): ArrayList<Usager>{
            val readable = DatabaseHandler(context).readableDatabase

            val cursor: Cursor = readable.rawQuery("SELECT * FROM ${DI.UsagerTable.TABLE_NAME}", null)

            val arrayList: ArrayList<Usager> = ArrayList<Usager>()

            try {
                if(cursor.moveToFirst()){
                    do {
                        val idUsager: Int = cursor.getInt(cursor.getColumnIndexOrThrow(DI.UsagerTable.COLUMN_ID_USAGER))
                        arrayList.add(
                            Usager(id= idUsager,
                                prenom = cursor.getString(cursor.getColumnIndexOrThrow(DI.UsagerTable.COLUMN_PRENOM)),
                                nom = cursor.getString(cursor.getColumnIndexOrThrow(DI.UsagerTable.COLUMN_NOM)),
                                courriel = cursor.getString(cursor.getColumnIndexOrThrow(DI.UsagerTable.COLUMN_COURRIEL)),
                                motdepasse = cursor.getString(cursor.getColumnIndexOrThrow(DI.UsagerTable.COLUMN_MOT_DE_PASSE)),
                                numeroTelephone = cursor.getString(cursor.getColumnIndexOrThrow(DI.UsagerTable.COLUMN_NUMERO_TELEPHONE)),
                                listeEvaluations = getEvaluationFromIDUsager(readable, context, idUsager, TypeUsager.RECEVEUR)
                            )
                        )
                    } while (cursor.moveToNext())
                }
            }
            catch (e: SQLiteException){
                Log.e("CoVoitError", e.message!!)
            }
            finally {
                cursor.close()
                readable.close()
            }

            return arrayList
        }

        /**
         * Private method to get an user id from the courriel
         */
        @Deprecated("Will be removed")
        private fun getUsagerID(context: Context, mail: String): Int{
            val query = """
                SELECT ${DI.UsagerTable.COLUMN_ID_USAGER}
                 FROM ${DI.UsagerTable.TABLE_NAME}
                  WHERE ${DI.UsagerTable.COLUMN_COURRIEL} = ?;
            """.trimIndent()

            val readable = DatabaseHandler(context).readableDatabase

            val cursor: Cursor = readable.rawQuery(query, arrayOf(mail))

            try {
                if(cursor.moveToFirst()) {
                    return cursor.getInt(cursor.getColumnIndexOrThrow(DI.UsagerTable.COLUMN_ID_USAGER))
                }
            }
            catch (e: SQLiteException){
                Log.e("CoVoitError", e.message!!)

            }
            finally {
                cursor.close()
                readable.close()
            }
            return -1
        }

        /**
         * Method to get all travels in the database
         *
         * @property context Context of the application
         * @return An ArrayList of Voyage
         */
        fun getVoyages(context: Context): ArrayList<Voyage>{
            val readable = DatabaseHandler(context).readableDatabase

            val cursor: Cursor = readable.rawQuery("SELECT * FROM ${DI.VoyageTable.TABLE_NAME}", null)

            val arrayList: ArrayList<Voyage> = ArrayList<Voyage>()

            try {
                if(cursor.moveToFirst()){
                    do {
                        val idVoyage: Int = cursor.getInt(cursor.getColumnIndexOrThrow(DI.VoyageTable.COLUMN_ID_VOYAGE))
                        arrayList.add(
                            Voyage(
                                id = idVoyage,
                                idConducteur = cursor.getInt(cursor.getColumnIndexOrThrow(DI.VoyageTable.COLUMN_ID_CONDUCTEUR)),
                                villeDepart = cursor.getString(cursor.getColumnIndexOrThrow(DI.VoyageTable.COLUMN_VILLE_DEPART)),
                                adresseDepart = cursor.getString(cursor.getColumnIndexOrThrow(DI.VoyageTable.COLUMN_ADRESSE_DEPART)),
                                date = LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(DI.VoyageTable.COLUMN_DATE))),
                                adresseArrivee = cursor.getString(cursor.getColumnIndexOrThrow(DI.VoyageTable.COLUMN_ADRESSE_ARRIVEE)),
                                cegepArrivee = cursor.getString(cursor.getColumnIndexOrThrow(DI.VoyageTable.COLUMN_CEGEP_ARRIVEE)),
                                prix = cursor.getFloat(cursor.getColumnIndexOrThrow(DI.VoyageTable.COLUMN_PRICE)),
                                nbMaxPassager = cursor.getInt(cursor.getColumnIndexOrThrow(DI.VoyageTable.COLUMN_NB_MAX_PASSAGER)),
                                nonFumeur = cursor.getInt(cursor.getColumnIndexOrThrow(DI.VoyageTable.COLUMN_NON_FUMEUR)) == 1,
                                accepteBagage = cursor.getInt(cursor.getColumnIndexOrThrow(DI.VoyageTable.COLUMN_ACCEPTE_BAGAGE)) == 1,
                                aAirClimatise = cursor.getInt(cursor.getColumnIndexOrThrow(DI.VoyageTable.COLUMN_AIR_CLIMATISE)) == 1,
                                listeIdPassagers = getIdPassagers(readable, context,  idVoyage)

                            )
                        )
                    } while (cursor.moveToNext())
                }
            }
            catch (e: SQLiteException){
                Log.e("CoVoitError", e.message!!)
            }
            finally {
                cursor.close()
                readable.close()
            }

            return arrayList
        }

        /**
         * Private method to get all evaluation from an user
         *
         * @property dbInstance Previous instance in case of the function called as a subquery
         * @property context Context of the application
         * @property idUsager id of the user
         * @property typeUsager Allow to get all Evaluation FROM the user or TO the User
         * @return An ArrayList of Evaluation
         */
        private fun getEvaluationFromIDUsager(dbInstance: SQLiteDatabase? = null, context: Context, idUsager: Int, typeUsager: TypeUsager = TypeUsager.DONNEUR) : ArrayList<Evaluation>{
            val readable: SQLiteDatabase = dbInstance ?: DatabaseHandler(context).readableDatabase

            val cursor: Cursor = readable.rawQuery(
                "SELECT * " +
                        "FROM ${DI.EvaluationTable.TABLE_NAME} " +
                        "WHERE ${typeUsager.type} = ?",
                arrayOf(idUsager.toString())
            )

            val arrayList: ArrayList<Evaluation> = ArrayList<Evaluation>()

            try {
                if(cursor.moveToFirst()){
                    do {
                        arrayList.add(
                            Evaluation(
                                idUsager = cursor.getInt(cursor.getColumnIndexOrThrow(DI.EvaluationTable.COLUMN_ID_USAGER_DONNEUR)),
                                note = cursor.getInt(cursor.getColumnIndexOrThrow(DI.EvaluationTable.COLUMN_NOTE))
                            )
                        )
                    } while (cursor.moveToNext())
                }
            }
            catch (e: SQLiteException){
                Log.e("CoVoitError", e.message!!)
            }
            finally {
                //If not a subquery
                if(dbInstance == null){
                    cursor.close()
                    readable.close()
                }
            }
            return arrayList
        }

        /**
         * Private method to get all id of a travel
         *
         * @property dbInstance Previous instance in case of the function called as a subquery
         * @property context Context of the application
         * @property idVoyage id of the travel to get the passengers of
         * @return An ArrayList of int with all id of the passengers
         */
        private fun getIdPassagers(dbInstance: SQLiteDatabase? = null, context: Context, idVoyage: Int): ArrayList<Int>{
            val readable: SQLiteDatabase = dbInstance ?: DatabaseHandler(context).readableDatabase

            val cursor: Cursor = readable.rawQuery("SELECT ${DI.PassagerVoyageTable.COLUMN_ID_USAGER} " +
                    "FROM ${DI.PassagerVoyageTable.TABLE_NAME} " +
                    "WHERE ${DI.PassagerVoyageTable.COLUMN_ID_VOYAGE} = ?", arrayOf(idVoyage.toString())
            )

            val arrayList: ArrayList<Int> = ArrayList<Int>()

            try {
                if(cursor.moveToFirst()){
                    do {
                        arrayList.add(cursor.getInt(cursor.getColumnIndexOrThrow(DI.PassagerVoyageTable.COLUMN_ID_USAGER)))
                    } while(cursor.moveToNext())
                }
            }
            catch (e: SQLiteException){
                Log.e("CoVoitError", e.message!!)
            }
            finally {
                //If not a subquery
                if(dbInstance == null){
                    cursor.close()
                    readable.close()
                }
            }

            return arrayList
        }

        /**
         * Method to insert an Evaluation
         *
         * @property context Context of the application
         * @property idUsagerReceveur The user id to the evaluation is
         * @property eval Evaluation object
         * @return Long number that is the id in the database of the row just inserted
         */
        fun insertEvaluation(context: Context, idUsagerReceveur: Int, eval: Evaluation): Long{
            val writable = DatabaseHandler(context).writableDatabase
            return try {
                val cv: ContentValues = ContentValues()
                cv.put(DI.EvaluationTable.COLUMN_NOTE, eval.note)
                cv.put(DI.EvaluationTable.COLUMN_ID_USAGER_DONNEUR, eval.idUsager)
                cv.put(DI.EvaluationTable.COLUMN_ID_USAGER_RECEVEUR, idUsagerReceveur)

                writable.insert(DI.EvaluationTable.TABLE_NAME, null, cv)
            } catch (e: SQLiteException){
                Log.e("ErrorCoVoit", e.message!!)
                -1
            } finally {
                writable.close()
            }

        }

        /**
         * Method to join a travel
         *
         * @property dbInstance Previous instance in case of the function called as a subquery
         * @property context Context of the application
         * @property idVoyage id of the travel that the user will join
         * @property idUsager id of the user that will join the travel
         * @return Long number that is the id in the database of the row just inserted
         */
        fun joindreVoyage(dbInstance: SQLiteDatabase? = null, context: Context, idVoyage: Int, idUsager: Int) : Long{
            val writable: SQLiteDatabase = dbInstance ?: DatabaseHandler(context).writableDatabase
            return try {
                val cv: ContentValues = ContentValues()
                cv.put(DI.PassagerVoyageTable.COLUMN_ID_VOYAGE, idVoyage)
                cv.put(DI.PassagerVoyageTable.COLUMN_ID_USAGER, idUsager)

                writable.insert(DI.PassagerVoyageTable.TABLE_NAME, null, cv)
            }
            catch (e: SQLiteException){
                Log.e("ErrorCoVoit", e.message!!)
                -1
            } finally {
                if(dbInstance == null){
                    writable.close()
                }
            }
        }

        /**
         * Method to leave a travel
         *
         * @property context Context of the application
         * @property idVoyage id of the travel that the user will leave
         * @property idUsager id of the user that will leave the travel
         * @return Int that is the number of row deleted in the database
         */
        fun quitterVoyage(context: Context, idVoyage: Int, idUsager: Int) : Int{
            val writable = DatabaseHandler(context).writableDatabase
            return try {
                writable.delete(DI.PassagerVoyageTable.TABLE_NAME,
                    "${DI.PassagerVoyageTable.COLUMN_ID_USAGER} = ? " +
                            "AND ${DI.PassagerVoyageTable.COLUMN_ID_VOYAGE} = ?",
                arrayOf(idUsager.toString(), idVoyage.toString())
                )
            }
            catch (e: SQLiteException){
                Log.e("ErrorCoVoit", e.message!!)
                -1
            } finally {
                writable.close()
            }
        }

        /**
         * Method to delete a user from the database
         *
         * @property context Context of the application
         * @property idUsager id of the user that we want to delete
         * @return True if the user has been delete
         */
        fun deleteUsager(context: Context, idUsager: Int) : Boolean{
            val writable = DatabaseHandler(context).writableDatabase

            try {
                writable.delete(DI.PassagerVoyageTable.TABLE_NAME,
                "${DI.PassagerVoyageTable.COLUMN_ID_USAGER} = ?",
                            arrayOf(idUsager.toString())
                )

                writable.delete(DI.VoyageTable.TABLE_NAME,
                    "${DI.VoyageTable.COLUMN_ID_CONDUCTEUR} = ?",
                            arrayOf(idUsager.toString())
                )

                writable.delete(DI.EvaluationTable.TABLE_NAME,
                    "${DI.EvaluationTable.COLUMN_ID_USAGER_DONNEUR} = ? OR " +
                            "${DI.EvaluationTable.COLUMN_ID_USAGER_RECEVEUR} = ?",
                            arrayOf(idUsager.toString(), idUsager.toString())
                            )

                DataManager.listeUsagers.remove(DataManager.getUsager(idUsager))

                return true
            }
            catch (e: SQLiteException){
                Log.e("ErrorCoVoit", e.message!!)
                return false
            }
            finally {
                writable.close()
            }
        }

        /**
         * Method to delete the database tables and recreate it
         *
         *@property context Context of the application
         */
        fun deleteDBAndRecreate(context: Context){
            deleteDB(context)

            val writable = DatabaseHandler(context).writableDatabase

            writable.execSQL(SQL_CREATE_ENTRIES_USAGER_TABLE)
            writable.execSQL(SQL_CREATE_ENTRIES_VOYAGE_TABLE)
            writable.execSQL(SQL_CREATE_ENTRIES_PASSAGER_VOYAGEUR_TABLE)
            writable.execSQL(SQL_CREATE_ENTRIES_EVALUATION_TABLE)

            writable.close()

            Log.d("CoVoitDebug", "Database created")
        }

        /**
         * Method to delete the database tables
         *
         *@property context Context of the application
         */
        fun deleteDB(context: Context){
            val req_DELETE_TABLE_USAGER = "DROP TABLE IF EXISTS ${DI.UsagerTable.TABLE_NAME}"
            val req_DELETE_TABLE_VOYAGE = "DROP TABLE IF EXISTS ${DI.VoyageTable.TABLE_NAME}"
            val req_DELETE_TABLE_PASSAGER_VOYAGE = "DROP TABLE IF EXISTS ${DI.PassagerVoyageTable.TABLE_NAME}"
            val req_DELETE_TABLE_EVALUATION = "DROP TABLE IF EXISTS ${DI.EvaluationTable.TABLE_NAME}"

            val writable = DatabaseHandler(context).writableDatabase

            writable.execSQL(req_DELETE_TABLE_PASSAGER_VOYAGE)
            writable.execSQL(req_DELETE_TABLE_VOYAGE)
            writable.execSQL(req_DELETE_TABLE_EVALUATION)
            writable.execSQL(req_DELETE_TABLE_USAGER)

            writable.close()
        }

        fun createTables(dbInstance : SQLiteDatabase){
            dbInstance.execSQL(SQL_CREATE_ENTRIES_USAGER_TABLE)
            dbInstance.execSQL(SQL_CREATE_ENTRIES_VOYAGE_TABLE)
            dbInstance.execSQL(SQL_CREATE_ENTRIES_PASSAGER_VOYAGEUR_TABLE)
            dbInstance.execSQL(SQL_CREATE_ENTRIES_EVALUATION_TABLE)
            Log.d(DEBUG_LOG_NAME, "Database created")
        }
    }


}