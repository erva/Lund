package io.erva.lund.storage

import android.content.Context
import io.erva.lund.R
import io.erva.lund.data.provider.BankSource

class PrefStorage {

    fun saveWidgetBank(context: Context, widgetId: Int, bank: BankSource) =
            getSharedPrefs(context).edit().putString(widgetId.toString(), bank.name).apply()

    fun getWidgetBank(context: Context, widgetId: Int): BankSource =
            BankSource.valueOf(getSharedPrefs(context).getString(widgetId.toString(), BankSource.UNDEFINED.name))

    fun removeWidgetBank(context: Context, widgetId: Int) =
            getSharedPrefs(context).edit().remove(widgetId.toString()).apply()

    private fun getSharedPrefs(context: Context) =
            context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
}