package com.yrenard.applicationcovoiturage

import android.app.Application
import android.content.Context

/**
 * Class extented from Application to everytime get the application context
 */
class ApplicationCoVoiturage : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: ApplicationCoVoiturage

        /**
         * Getter that return the application context
         * @return ApplicationContext object
         */
        fun getApplicationContext(): Context{
            return instance.applicationContext
        }
    }
}