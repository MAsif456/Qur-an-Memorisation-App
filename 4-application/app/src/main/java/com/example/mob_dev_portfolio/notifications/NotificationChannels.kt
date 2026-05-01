package com.example.mob_dev_portfolio.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mob_dev_portfolio.R

/** Notification channels. Registered once from [com.example.mob_dev_portfolio.QuranMemoriserApp.onCreate]. */
object NotificationChannels {

    const val REVISION_REMINDER = "revision_reminder"

    fun registerAll(context: Context) {
        val manager = NotificationManagerCompat.from(context)
        val revision = NotificationChannelCompat.Builder(
            REVISION_REMINDER,
            NotificationManager.IMPORTANCE_DEFAULT,
        )
            .setName(context.getString(R.string.notif_channel_revision_name))
            .setDescription(context.getString(R.string.notif_channel_revision_description))
            .setShowBadge(true)
            .build()
        manager.createNotificationChannel(revision)
    }
}
