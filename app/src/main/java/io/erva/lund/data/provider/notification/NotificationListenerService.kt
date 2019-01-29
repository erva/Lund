package io.erva.lund.data.provider.notification

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class MonobankNotificationListenerService : NotificationListenerService() {

    private val monobankPackage = "com.ftband.mono"

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName == monobankPackage) {
            val plainNotification = PlainNotification(
                    sbn.packageName,
                    sbn.postTime,
                    sbn.notification.extras.getCharSequence(Notification.EXTRA_TEXT) as String,
                    sbn.notification.extras.getCharSequence(Notification.EXTRA_TITLE) as String)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        val activeNotifications = this.activeNotifications
        Log.d("here", "here1")

        //sbn.getPackageName() = com.ftband.mono
        //sbn.getPostTime() = 1547978738715
        //sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT) = COOP MARTENSTORGET\nБаланс 3 943.87₴
        //sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE) = 🍞 56.00SEK Кешбэк: 3.50₴
    }
}