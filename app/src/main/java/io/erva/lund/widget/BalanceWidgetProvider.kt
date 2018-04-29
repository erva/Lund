package io.erva.lund.widget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.provider.Telephony
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import io.erva.lund.R
import io.erva.lund.data.DataItem
import io.erva.lund.data.DataProviderFactory
import java.text.SimpleDateFormat

class BalanceWidgetProvider : AppWidgetProvider() {

    private val updateHandler = Handler()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            updateHandler.postDelayed({
                val appWidgetManager = AppWidgetManager.getInstance(context)
                onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(ComponentName(context, BalanceWidgetProvider::class.java)))
            }, 1000)
        } else {
            super.onReceive(context, intent)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach {
            val remoteView = RemoteViews(context.packageName, R.layout.widget_bank_sms)
            remoteView.setRemoteAdapter(R.id.item_list, Intent(context, BalanceAdapterService::class.java))
            appWidgetManager.updateAppWidget(it, remoteView);
            appWidgetManager.notifyAppWidgetViewDataChanged(it, R.id.item_list);
        }
    }
}

class BalanceAdapterService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return BalanceListFactory(applicationContext)
    }
}

class BalanceListFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

    private val RECOST_DELAY = 2 * 60 * 60 * 1000
    private val items: MutableList<DataItem> = mutableListOf()

    override fun onCreate() = Unit
    override fun getLoadingView() = null
    override fun getItemId(position: Int) = position.toLong()
    override fun onDataSetChanged() = fetchData()
    override fun hasStableIds() = true
    override fun getCount() = items.size
    override fun getViewTypeCount() = 1
    override fun onDestroy() = Unit

    @SuppressLint("SimpleDateFormat")
    override fun getViewAt(position: Int): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.item_bank_sms)
        val item = items[position]

        val dateSent = SimpleDateFormat("MM/dd").format(item.dateSent)
        val timeSent = SimpleDateFormat("HH:mm").format(item.dateSent)

        remoteViews.apply {
            setTextViewText(R.id.card, item.card)
            setTextViewText(R.id.date, "$dateSent $timeSent")
            setTextViewText(R.id.balance, "%.2f".format(item.balance))
            val isPlus = item.difference > 0
            setTextViewText(R.id.difference,
                    (if (isPlus) "+" else "") + "%.2f".format(item.difference))
            setTextColor(R.id.difference, context.getColor(
                    if (isPlus) R.color.color_income else R.color.color_expense))

            val timeDiff = Math.abs(item.dateSent.time - item.parsedDate.time)
            val moreThenDay = timeDiff > RECOST_DELAY
            setViewVisibility(R.id.recost, if (moreThenDay) View.VISIBLE else View.GONE)
        }
        return remoteViews
    }

    private fun fetchData() {
        items.clear()
        items.addAll(DataProviderFactory.getDataProvider().provideData(context))
    }
}