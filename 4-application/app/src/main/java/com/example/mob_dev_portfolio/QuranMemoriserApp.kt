package com.example.mob_dev_portfolio

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.mob_dev_portfolio.notifications.NotificationChannels
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application entry point.
 *
 * Registers notification channels on start and provides a [HiltWorkerFactory]
 * to WorkManager so [androidx.hilt.work.HiltWorker]-annotated workers get
 * constructor injection.
 */
@HiltAndroidApp
class QuranMemoriserApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        NotificationChannels.registerAll(this)
    }
}
