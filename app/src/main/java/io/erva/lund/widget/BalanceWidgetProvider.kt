package io.erva.lund.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
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
import android.widget.Toast
import io.erva.lund.R
import io.erva.lund.data.mapper.DataItem
import io.erva.lund.data.provider.DataProvider
import io.erva.lund.data.provider.DataProviderFactory
import io.erva.lund.storage.pref.PrefStorage

const val ACTION_ON_CLICK = "io.erva.lund.widget.ACTION_ON_CLICK"
const val ACTION_ON_NEW_NOTIFICATION = "io.erva.lund.widget.ACTION_ON_NEW_NOTIFICATION"
const val EXTRA_DATA_ITEM = "EXTRA_DATA_ITEM"

class BalanceWidgetProvider : AppWidgetProvider() {

    private val updateHandler = Handler()

    override fun onReceive(context: Context, intent: Intent) {
        when {
            intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION -> onDataUpdated(context)
            intent.action == ACTION_ON_NEW_NOTIFICATION -> onDataUpdated(context)
            intent.action == ACTION_ON_CLICK -> onClick(context, intent.getParcelableExtra(EXTRA_DATA_ITEM))
            else -> super.onReceive(context, intent)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach {
            val remoteView = RemoteViews(context.packageName, R.layout.widget_transactions)
            val adapterIntent = Intent(context, TransactionsAdapterService::class.java)
            adapterIntent.data = Uri.fromParts("content", it.toString(), null)
            remoteView.setRemoteAdapter(R.id.widget_transactions_list, adapterIntent)

            val listClickIntent = Intent(context, BalanceWidgetProvider::class.java)
            listClickIntent.action = ACTION_ON_CLICK
            val listClickPIntent = PendingIntent.getBroadcast(context, 0,
                    listClickIntent, 0)
            remoteView.setPendingIntentTemplate(R.id.widget_transactions_list, listClickPIntent)

            appWidgetManager.updateAppWidget(it, remoteView)
            appWidgetManager.notifyAppWidgetViewDataChanged(it, R.id.widget_transactions_list)
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        context?.let {
            appWidgetIds?.forEach { widgetId ->
                PrefStorage().removeWidgetBank(context, widgetId)
            }
        }
    }

    private fun onDataUpdated(context: Context) {
        updateHandler.postDelayed({
            val appWidgetManager = AppWidgetManager.getInstance(context)
            onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(
                    ComponentName(context, BalanceWidgetProvider::class.java)))
        }, 1000)
    }

    private fun onClick(context: Context, dataItem: DataItem?) {
        dataItem?.let {
            if (!dataItem.details.isNullOrEmpty()) Toast.makeText(context, dataItem.details, Toast.LENGTH_SHORT).show()
            else openSmsApp(context, dataItem)
        }
    }

    private fun openSmsApp(context: Context, dataItem: DataItem) {
        val smsAppIntent = Intent(Intent.ACTION_VIEW,
                Uri.fromParts("sms", dataItem.address, null))
        smsAppIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(smsAppIntent)
    }
}

class TransactionsAdapterService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return TransactionsListFactory(applicationContext, intent)
    }
}

class TransactionsListFactory(private val context: Context, private val intent: Intent)
    : RemoteViewsService.RemoteViewsFactory {

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
        // TODO !!
        val appWidgetId = Integer.valueOf(intent.data!!.schemeSpecificPart)
        val bank = PrefStorage().getWidgetBank(context, appWidgetId)
        dataProvider = DataProviderFactory.getDataProvider(bank)
        items.addAll(dataProvider.provide(context))
    }

    @SuppressLint("SimpleDateFormat")
    override fun getViewAt(position: Int): RemoteViews = dataProvider.getLayout()
            .layoutData(context, items[position],
                    Intent().apply { putExtra(EXTRA_DATA_ITEM, items[position]) }
            )
}