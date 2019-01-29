package io.erva.lund.screen

import android.Manifest
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import io.erva.celladapter.x.Cell
import io.erva.celladapter.x.CellAdapter
import io.erva.lund.R
import io.erva.lund.data.provider.Bank
import io.erva.lund.data.provider.SOURCE
import io.erva.lund.storage.PrefStorage
import kotlinx.android.synthetic.main.activity_preference.bank_list

private const val REQUEST_SMS_PERMISSION = 2

class PreferenceActivity : AppCompatActivity() {

    private var adapter: CellAdapter = CellAdapter().let {
        it.cell(BankItemCell::class) {
            item(BankItemModel::class)
            listener(object : Cell.Listener<BankItemModel> {
                override fun onCellClicked(item: BankItemModel) {
                    onProviderSelected(item.bank)
                }
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference)
        checkPermissions()
    }

    //region permissions
    private fun checkPermissions() {
        if (!isSmsPermissionGranted()) {
            requestReadAndSendSmsPermission()
        } else {
            showProviders()
        }
    }

    private fun isSmsPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this@PreferenceActivity, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this@PreferenceActivity, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestReadAndSendSmsPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS), REQUEST_SMS_PERMISSION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_SMS_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    showProviders()
                } else {
                    onCanceled()
                }
            }
        }
    }
    //endregion

    private fun showProviders() {
        bank_list.layoutManager = LinearLayoutManager(this)
        bank_list.adapter = adapter
        adapter.items.addAll(arrayOf(
                BankItemModel(R.drawable.ic_bannk_pumb, "PUMB", Bank.PUMB, SOURCE.SMS),
                BankItemModel(R.drawable.ic_bank_privat, "Privat bank", Bank.PRIVATBANK, SOURCE.SMS),
                BankItemModel(R.drawable.ic_bank_ukrsib, "UkrSib bank", Bank.UKRSIBBANK, SOURCE.SMS),
                BankItemModel(R.drawable.ic_bank_aval, "Raiffeisen Bank Aval", Bank.AVALBANK, SOURCE.SMS),
                BankItemModel(R.drawable.ic_bank_mono, "Monobank", Bank.MONOBANK, SOURCE.NOTIFICATION)
        ))
        adapter.notifyDataSetChanged()
    }

    private fun onProviderSelected(bank: Bank) {
        val intent = intent
        intent.extras?.apply {
            val appWidgetId = getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            PrefStorage().saveWidgetBank(applicationContext, appWidgetId, bank)
            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(Activity.RESULT_OK, resultValue)

            val forceUpdateIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            forceUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, arrayOf(appWidgetId))
            sendBroadcast(forceUpdateIntent)

            finish()
        }
    }

    private fun onCanceled() {
        finish()
    }
}