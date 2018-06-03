package io.erva.lund.widget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.provider.Telephony
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import io.erva.lund.R
import io.erva.lund.data.DataProviderFactory
import io.erva.lund.data.mapper.DataItem
import io.erva.lund.storage.PrefStorage
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class BalanceWidgetProvider : AppWidgetProvider() {

    private val updateHandler = Handler()

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("->")
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
        Timber.d(Arrays.toString(appWidgetIds))
        appWidgetIds.forEach {
            Timber.d("update %d", it)
            val remoteView = RemoteViews(context.packageName, R.layout.widget_bank_sms)
            val adapterIntent = Intent(context, BalanceAdapterService::class.java)
            adapterIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, it);
            remoteView.setRemoteAdapter(R.id.item_list, adapterIntent)
            appWidgetManager.updateAppWidget(it, remoteView)
            appWidgetManager.notifyAppWidgetViewDataChanged(it, R.id.item_list);
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        context?.let {
            appWidgetIds?.forEach {
                Timber.d("delete %d", it)
                PrefStorage().removeWidgetBank(context, it)
            }
        }
    }
}

class BalanceAdapterService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        Timber.d("->")
        return BalanceListFactory(applicationContext, intent)
    }
}

class BalanceListFactory(private val context: Context, private val intent: Intent) : RemoteViewsService.RemoteViewsFactory {

    private val RECOST_DELAY = 2 * 60 * 60 * 1000
    private val items: MutableList<DataItem> = mutableListOf()

    override fun onCreate(): Unit = Timber.d("->")
    override fun getLoadingView() = null
    override fun getItemId(position: Int) = position.toLong()
    override fun onDataSetChanged() = fetchData()
    override fun hasStableIds() = true
    override fun getCount() = items.size
    override fun getViewTypeCount() = 1
    override fun onDestroy() = Unit

    private fun fetchData() {
        Timber.d("->")
        items.clear()

        val widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        val data = PrefStorage().getWidgetBank(context, widgetId)
        Timber.d("fetch for widgetId %d, bank %s", widgetId, data.name)
        DataProviderFactory.getDataProvider(context, data)?.let { items.addAll(it.provide()) }
    }

    @SuppressLint("SimpleDateFormat")
    override fun getViewAt(position: Int): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_item_bank_sms)
        val item = items[position]

        val dateSent = SimpleDateFormat("MM/dd").format(item.dateSent)
        val timeSent = SimpleDateFormat("HH:mm").format(item.dateSent)

        remoteViews.apply {
            setTextViewText(R.id.card, item.card)
            setTextViewText(R.id.date, "$dateSent $timeSent")

            val balanceText = SpannableString("%.2f".format(item.balance))
            balanceText.setSpan(RelativeSizeSpan(0.75f),
                    balanceText.indexOf('.'),
                    balanceText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            setTextViewText(R.id.balance, balanceText)

            val isPlus = item.difference > 0
            val diffText = SpannableString((if (isPlus) "+" else "") + "%.2f".format(item.difference))
            diffText.setSpan(RelativeSizeSpan(0.75f),
                    diffText.indexOf('.'),
                    diffText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            setTextViewText(R.id.difference, diffText)
            setTextColor(R.id.difference, context.getColor(if (isPlus) R.color.color_income else R.color.color_expense))

            val timeDiff = Math.abs(item.dateSent.time - item.parsedDate.time)
            val moreThenDay = timeDiff > RECOST_DELAY
            setViewVisibility(R.id.recost, if (moreThenDay) View.VISIBLE else View.GONE)
        }
        return remoteViews
    }
}