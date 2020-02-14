package io.erva.lund.screen

import android.Manifest
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import io.erva.celladapter.x.Cell
import io.erva.celladapter.x.CellAdapter
import io.erva.lund.R
import io.erva.lund.data.provider.Bank
import io.erva.lund.data.provider.SOURCE
import io.erva.lund.storage.pref.PrefStorage
import kotlinx.android.synthetic.main.activity_preference.bank_list

class PreferenceActivity : AppCompatActivity() {

  private var isNotificationRequested = false

  private val bankModels = listOf(
      BankItemModel(R.drawable.ic_bannk_pumb, "PUMB", Bank.PUMB, SOURCE.SMS),
      BankItemModel(R.drawable.ic_bank_privat, "Privat bank", Bank.PRIVATBANK, SOURCE.SMS),
      BankItemModel(R.drawable.ic_bank_ukrsib, "UkrSib bank", Bank.UKRSIBBANK, SOURCE.SMS),
      BankItemModel(R.drawable.ic_bank_aval, "Raiffeisen Bank Aval", Bank.AVALBANK, SOURCE.SMS),
      BankItemModel(R.drawable.ic_bank_mono, "Monobank", Bank.MONOBANK, SOURCE.NOTIFICATION)
  )

  private var adapter: CellAdapter = CellAdapter().let {
    it.cell(BankItemCell::class) {
      item(BankItemModel::class)
      listener(object : Cell.Listener<BankItemModel> {
        override fun onCellClicked(item: BankItemModel) {
          onProviderSelected(item)
        }
      })
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_preference)

    bank_list.layoutManager = LinearLayoutManager(this)
    bank_list.adapter = adapter
    adapter.items.addAll(bankModels)
    adapter.notifyDataSetChanged()
  }

  public override fun onSaveInstanceState(savedInstanceState: Bundle) {
    savedInstanceState.putBoolean("a", isNotificationRequested)
    super.onSaveInstanceState(savedInstanceState)
  }

  public override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    isNotificationRequested = savedInstanceState.getBoolean("a")
  }

  override fun onResume() {
    super.onResume()
    Log.d("pajero", "here")
    if (isNotificationRequested) {
      bankModels.firstOrNull { it.source == SOURCE.NOTIFICATION }
          ?.let {
            onRequestNotificationAccessResult(it)
          }
    }
  }

  private fun onProviderSelected(bankModel: BankItemModel) {
    when (bankModel.source) {
      SOURCE.SMS -> {
        if (!isSmsPermissionGranted()) {
          requestReadAndSendSmsPermission(bankModels.indexOf(bankModel))
          return
        }
      }
      SOURCE.NOTIFICATION -> {
        if (!isNotificationAccessGranted()) {
          requestNotificationAccess()
          return
        }
      }
    }

    val intent = intent
    intent.extras?.apply {
      val appWidgetId =
        getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
      PrefStorage().saveWidgetBank(applicationContext, appWidgetId, bankModel.bank)
      val resultValue = Intent()
      resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
      setResult(Activity.RESULT_OK, resultValue)

      val forceUpdateIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
      forceUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, arrayOf(appWidgetId))
      sendBroadcast(forceUpdateIntent)

      finish()
    }
  }

  //region sms permission
  private fun isSmsPermissionGranted(): Boolean {
    return ContextCompat.checkSelfPermission(
        this@PreferenceActivity,
        Manifest.permission.READ_SMS
    ) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(
            this@PreferenceActivity,
            Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED
  }

  /**
   * requestCode is index in bankModels
   */
  private fun requestReadAndSendSmsPermission(requestCode: Int) {
    ActivityCompat.requestPermissions(
        this, arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS),
        requestCode
    )
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    if (IntRange(0, bankModels.size - 1).contains(requestCode)) {
      if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
        onProviderSelected(bankModels[requestCode])
      } else {
        onCanceled()
      }
    }
  }
  //endregion

  //region notification permission
  private fun isNotificationAccessGranted(): Boolean {
    return NotificationManagerCompat.getEnabledListenerPackages(application)
        .contains(packageName)
  }

  private fun requestNotificationAccess() {
    startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS))
  }

  private fun onRequestNotificationAccessResult(bankModel: BankItemModel) {
    if (isNotificationAccessGranted()) {
      onProviderSelected(bankModel)
    } else {
      onCanceled()
    }
  }
  //endregion

  private fun onCanceled() {
    finish()
  }
}