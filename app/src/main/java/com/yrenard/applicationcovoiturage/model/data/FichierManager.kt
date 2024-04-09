package com.yrenard.applicationcovoiturage.model.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.yrenard.applicationcovoiturage.ApplicationCoVoiturage
import com.yrenard.applicationcovoiturage.model.data.FichierManager.deleteFilesFromSharedPrefsDirectory
import java.io.File

enum class SharedPreferenceType(val type: Int) {
    NORMAL(0),
    ENCRYPTED(1)
}

object FichierManager : SharedPreferencesDelete(){

    fun getSharedPreferencesFiles(ctx: Context): SharedPreferences {
        return ctx.getSharedPreferences(SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
    }

    fun getEncryptedSharedPreferencesFiles(ctx: Context): SharedPreferences {
        return EncryptedSharedPreferences.create(
            ENC_SHARED_PREFERENCES_FILE_NAME,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            ctx,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun sauvegarderConfig(
        ctx: Context,
        field: String,
        value: String,
        mode: SharedPreferenceType = SharedPreferenceType.NORMAL
    ): Boolean {
        return if (mode.type == 1) getEncryptedSharedPreferencesFiles(ctx) //Get file
            .edit() //Switch in edit mode
            .putString(field, value) //Set the new or edit the field with the value
            .commit() //Commit change (return boolean if commited)
        else getSharedPreferencesFiles(ctx)
            .edit()
            .putString(field, value)
            .commit()
    }

    fun lireConfig(
        ctx: Context,
        field: String,
        mode: SharedPreferenceType = SharedPreferenceType.NORMAL
    ): String? {
        return if (mode.type == 1)
            getEncryptedSharedPreferencesFiles(ctx)
                .getString(field, GLOB_SHARE_PREFERENCE_DEFAULT_STRING)
        else getSharedPreferencesFiles(ctx)
            .getString(field, GLOB_SHARE_PREFERENCE_DEFAULT_STRING)
    }

    fun deleteSharedFiles() : Boolean{
        return this.
        deleteFilesFromSharedPrefsDirectory(ApplicationCoVoiturage.getApplicationContext())
    }

}
