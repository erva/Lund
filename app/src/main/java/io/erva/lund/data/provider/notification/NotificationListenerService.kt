package io.erva.lund.data.provider.notification

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import io.erva.lund.storage.room.AppDatabase
import io.erva.lund.storage.room.NotificationEntity
import io.erva.lund.widget.ACTION_ON_NEW_NOTIFICATION

const val MONOBANK_PACKAGE = "com.ftband.mono"
class MonobankNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName == MONOBANK_PACKAGE) {
            val notificationEntity = NotificationEntity(
                    packageName = sbn.packageName,
                    postTime = sbn.postTime,
                    text = sbn.notification.extras.getCharSequence(Notification.EXTRA_TEXT) as String,
                    title = sbn.notification.extras.getCharSequence(Notification.EXTRA_TITLE) as String)

            AppDatabase.getInstance(applicationContext).notificationDao().insert(notificationEntity)
            sendBroadcast(Intent(ACTION_ON_NEW_NOTIFICATION))
        }
    }
}