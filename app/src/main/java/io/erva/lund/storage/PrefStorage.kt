package io.erva.lund.storage

import android.content.Context
import io.erva.lund.R
import io.erva.lund.data.Data
import timber.log.Timber

class PrefStorage {

    fun saveWidgetBank(context: Context, widgetId: Int, data: Data) {
        Timber.d(widgetId.toString() + " " + data.name)
        getSharedPrefs(context).edit().putString(widgetId.toString(), data.name).apply()
    }

    fun getWidgetBank(context: Context, widgetId: Int): Data {
        val a = Data.valueOf(getSharedPrefs(context).getString(widgetId.toString(), Data.UNDEFINED.name))
        Timber.d(widgetId.toString() + " " + a.name)
        return a
    }

    fun removeWidgetBank(context: Context, widgetId: Int) {

        Timber.d(widgetId.toString())
        getSharedPrefs(context).edit().remove(widgetId.toString()).apply()

    }

    private fun getSharedPrefs(context: Context) =
            context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
}