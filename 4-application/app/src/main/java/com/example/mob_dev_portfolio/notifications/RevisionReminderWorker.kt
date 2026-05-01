package com.example.mob_dev_portfolio.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mob_dev_portfolio.MainActivity
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.data.repository.RevisionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Posts a reminder when ayahs are due. Returns success when the permission
 * is missing so WorkManager doesn't retry pointlessly: the user can grant
 * notifications later from system settings.
 */
@HiltWorker
class RevisionReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val revisionRepository: RevisionRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val isTest = inputData.getBoolean(NotificationScheduler.KEY_TEST, false)
        val due = if (isTest) TEST_FAKE_DUE else revisionRepository.countDueNow()
        if (due <= 0) return Result.success()
        if (!hasNotificationPermission()) return Result.success()
        post(due)
        return Result.success()
    }

    private fun post(due: Int) {
        val context = applicationContext
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val pending = PendingIntent.getActivity(context, REQUEST_CODE, openAppIntent, flags)
        val notification = NotificationCompat.Builder(context, NotificationChannels.REVISION_REMINDER)
            .setSmallIcon(R.drawable.ic_nav_tracker)
            .setContentTitle(context.getString(R.string.notif_revision_title))
            .setContentText(context.getString(R.string.notif_revision_body_format, due))
            .setContentIntent(pending)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    private companion object {
        const val NOTIFICATION_ID = 1001
        const val REQUEST_CODE = 1001
        const val TEST_FAKE_DUE = 1
    }
}
