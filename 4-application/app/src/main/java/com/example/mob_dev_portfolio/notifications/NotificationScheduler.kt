package com.example.mob_dev_portfolio.notifications

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Schedules [RevisionReminderWorker] via WorkManager so reminders survive
 * reboots and process death. Calling [schedule] again replaces the
 * existing schedule. [runNow] enqueues a one-shot bypassing the due-count
 * check so the user can verify notifications are wired up.
 */
@Singleton
class NotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun schedule(time: LocalTime) {
        val delay = computeInitialDelay(time)
        val request = PeriodicWorkRequestBuilder<RevisionReminderWorker>(
            REPEAT_INTERVAL_HOURS,
            TimeUnit.HOURS,
        )
            .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )
    }

    fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    /** Fires the reminder worker immediately as a one-shot, in test mode. */
    fun runNow() {
        val data = Data.Builder().putBoolean(KEY_TEST, true).build()
        val request = OneTimeWorkRequestBuilder<RevisionReminderWorker>()
            .setInputData(data)
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }

    private fun computeInitialDelay(time: LocalTime): Duration {
        val zone = ZoneId.systemDefault()
        val now = LocalDateTime.now(zone)
        val target = LocalDate.now(zone).atTime(time).let {
            if (it.isAfter(now)) it else it.plusDays(1)
        }
        return Duration.between(now, target)
    }

    companion object {
        /** Input-data key set by [runNow] so the worker bypasses the due-count check. */
        const val KEY_TEST: String = "test_mode"

        private const val WORK_NAME = "revision_reminder_daily"
        private const val REPEAT_INTERVAL_HOURS: Long = 24L
    }
}
