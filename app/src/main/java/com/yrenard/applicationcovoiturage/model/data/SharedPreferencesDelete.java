package com.yrenard.applicationcovoiturage.model.data;

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.File;
import java.util.Objects;

public abstract class SharedPreferencesDelete {

    /**
     * Method to deletes all preferences files in data/data/project/shared_prefs/
     *
     * @param ctx : Context of the application
     */
    protected boolean deleteFilesFromSharedPrefsDirectory(Context ctx) {
        @SuppressLint("SdCardPath") File dir =
                new File("/data/data/com.yrenard.applicationcovoiturage/shared_prefs/");
        String[] children = dir.list();
        for (int i = 0; i < Objects.requireNonNull(children).length; i++) {
            // clear each preference file
            ctx.getSharedPreferences(children[i].replace(".xml", ""), Context.MODE_PRIVATE).edit().clear().commit();
            //delete the file
            new File(dir, children[i]).delete();
        }
        return false;
    }
}
