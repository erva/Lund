package io.erva.lund.widget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.provider.Telephony
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import io.erva.lund.R
import io.erva.lund.data.DataProvider
import io.erva.lund.data.DataProviderFactory
import io.erva.lund.data.mapper.DataItem
import io.erva.lund.storage.PrefStorage

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
            val remoteView = RemoteViews(context.packageName, R.layout.widget_transactions)
            val adapterIntent = Intent(context, TransactionsAdapterService::class.java)
            adapterIntent.data = Uri.fromParts("content", it.toString(), null)
            remoteView.setRemoteAdapter(R.id.widget_transactions_list, adapterIntent)
            appWidgetManager.updateAppWidget(it, remoteView)
            appWidgetManager.notifyAppWidgetViewDataChanged(it, R.id.widget_transactions_list);
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        context?.let {
            appWidgetIds?.forEach {
                PrefStorage().removeWidgetBank(context, it)
            }
        }
    }
}

class TransactionsAdapterService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return TransactionsListFactory(applicationContext, intent)
    }
}

class TransactionsListFactory(private val context: Context, private val intent: Intent) : RemoteViewsService.RemoteViewsFactory {

    private val items: MutableList<DataItem> = mutableListOf()
    private lateinit var dataProvider: DataProvider

    override fun onCreate() = Unit
    override fun getLoadingView() = null
    override fun getItemId(position: Int) = position.toLong()
    override fun onDataSetChanged() = fetchData()
    override fun hasStableIds() = true
    override fun getCount() = items.size
    override fun getViewTypeCount() = 1
    override fun onDestroy() = Unit

    private fun fetchData() {
        items.clear()
        val appWidgetId = Integer.valueOf(intent.data.schemeSpecificPart)
        val data = PrefStorage().getWidgetBank(context, appWidgetId)
        dataProvider = DataProviderFactory.getDataProvider(context, data)
        items.addAll(dataProvider.provide())
    }

    @SuppressLint("SimpleDateFormat")
    override fun getViewAt(position: Int): RemoteViews = dataProvider.getLayout().layoutData(context, items[position])
}