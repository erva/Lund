package io.erva.lund.storage.pref

import android.content.Context
import io.erva.lund.R
import io.erva.lund.data.provider.Bank

class PrefStorage {

  fun saveWidgetBank(context: Context, widgetId: Int, bank: Bank) =
    getSharedPrefs(context).edit().putString(widgetId.toString(), bank.name).apply()

  // TODO !!
  fun getWidgetBank(context: Context, widgetId: Int): Bank =
    Bank.valueOf(getSharedPrefs(context).getString(widgetId.toString(), Bank.UNDEFINED.name)!!)

  fun removeWidgetBank(context: Context, widgetId: Int) =
    getSharedPrefs(context).edit().remove(widgetId.toString()).apply()

  private fun getSharedPrefs(context: Context) =
    context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
}